/*
 * This file is part of JAVI.
 *
 * JAVI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * JAVI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JAVI.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package tk.ivybits.javi.media.subtitle;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Parser for Advanced SubStation Alpha subtitles.
 *
 * @since 1.0
 */
public class DonkeyParser {
    protected String version;
    protected ArrayList<String> format;
    protected Style defaultStyle;
    protected HashMap<String, Style> styles = new HashMap<String, Style>();

    /**
     * Initializes a subtitle from its header.
     *
     * @param header the subtitle header.
     */
    public DonkeyParser(String header) {
        defaultStyle = new Style();
        defaultStyle.name = "Default";
        defaultStyle.font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        defaultStyle.primaryColor = Color.WHITE;
        defaultStyle.secondaryColor = Color.WHITE;
        defaultStyle.outlineColor = Color.BLACK;
        defaultStyle.backColor = Color.BLACK;
        for (String line : header.split("\\r?\\n")) {
            processLine(line);
        }
    }

    /**
     * Processes a format line.
     *
     * @param data A format line for the subsequent lines of text.
     */
    protected void processFormat(String data) {
        format = new ArrayList<String>(Arrays.asList(data.split("\\s*,\\s*")));
    }

    /**
     * Loads a line of SubStation Alpha command into a key-value mapping based on the current format.
     *
     * @param line Command line to parse.
     * @return A map of data stored in the line.
     */
    protected HashMap<String, String> parseWithFormat(String line) {
        HashMap<String, String> map = new HashMap<String, String>();
        String[] data = line.split("\\s*,\\s*");
        int size = Math.min(format.size(), data.length);
        for (int i = 0; i < size; ++i) {
            map.put(format.get(i), data[i]);
        }
        return map;
    }

    /**
     * Parses a SubStation Alpha integer literal.
     *
     * @param number An integer literal to parse, {@code &H} at the beginning means hex.
     * @return Parsed integer.
     */
    protected int parseInt(String number) {
        return Integer.parseInt(number.substring(2), number.startsWith("&H") ? 16 : 10);
    }

    /**
     * Parses a style line.
     *
     * @param line The style line.
     */
    protected void processStyle(String line) {
        HashMap<String, String> map = parseWithFormat(line);
        Style style = new Style();
        String name = map.get("Name");
        int fontStyle = 0;
        style.primaryColor = new Color(parseInt(getWithDefault(map, "PrimaryColour", "&HFFFFFF")));
        style.secondaryColor = new Color(parseInt(getWithDefault(map, "SecondaryColour", "&HFFFFFF")));
        style.outlineColor = new Color(parseInt(getWithDefault(map, "OutlineColour", "0")));
        style.backColor = new Color(parseInt(getWithDefault(map, "BackColour", "0")));
        if (parseInt(getWithDefault(map, "Bold", "0")) != 0)
            fontStyle &= Font.BOLD;
        if (parseInt(getWithDefault(map, "Italic", "0")) != 0)
            fontStyle &= Font.ITALIC;
        String fontName = getWithDefault(map, "Fontname", Font.SANS_SERIF);
        int fontSize = parseInt(getWithDefault(map, "Fontsize", "16"));
        if ("Default".equals(name) && "Arial".equals(fontName) && style.primaryColor == Color.WHITE &&
                style.secondaryColor == Color.WHITE && style.outlineColor == Color.BLACK &&
                style.backColor == Color.BLACK) {
            // FFmpeg's default font, for the subtitles converted to Donkey internally
            styles.put(name, defaultStyle);
        } else {
            Font font = new Font(fontName, fontStyle, fontSize);
            style.name = name;
            style.font = font;
            styles.put(name, style);
        }
    }

    /**
     * Dispatched of a line of subtitle.
     *
     * @param line A raw line of Substation Alpha.
     */
    protected void processLine(String line) {
        if (line.isEmpty() || line.startsWith("["))
            return;
        int colon = line.indexOf(":");
        if (colon == -1)
            return;
        String command = line.substring(0, colon);
        String data = line.substring(colon + 1).trim();
        //System.out.println("Parsed: Command: " + command + ", Data: " + data);
        if (command.equals("ScriptType")) version = data;
        else if (command.equals("Format")) processFormat(data);
        else if (command.equals("Style")) processStyle(data);
    }

    /**
     * Parses a SubStation Alpha timestamp into milliseconds since start of video.
     *
     * @param ts SubStation Alpha timestamp.
     * @return ts represented in milliseconds since start of video.
     */
    protected long parseTimeStamp(String ts) {
        String[] time = ts.split(":");
        return Integer.parseInt(time[0]) * 3600000 + Integer.parseInt(time[1]) * 60000 + (int) (Double.parseDouble(time[2]) * 1000);
    }

    /**
     * Processes a line of SubStation Alpha dialog.
     *
     * @param line The line of dialog.
     * @return The parsed line of subtitle.
     */
    public DonkeySubtitle processDialog(String line) {
        HashMap<String, String> map = parseWithFormat(line);
        String style = map.get("Style");
        if (style.startsWith("*"))
            style = style.substring(1);
        String text = map.get("Text").replace("\\n", "\n"); // \n is the embedded new line
        long start = map.containsKey("Start") ? parseTimeStamp(map.get("Start")) : 0;
        long end = map.containsKey("End") ? parseTimeStamp(map.get("End")) : 0;
        return new DonkeySubtitle(this, styles.get(style), start, end, text);
    }

    /**
     * Gets a helper to draw a subtitle parsed by this parser.
     *
     * @return A {@code DrawHelper}.
     */
    public DrawHelper getDrawHelper() {
        return new DrawHelper();
    }

    /**
     * Debug method to dump the subtitle header.
     *
     * @return The subtitle header abstract representation.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Version: ").append(version).append("\n");
        builder.append("Styles:\n");
        for (Map.Entry<String, Style> styleEntry : styles.entrySet()) {
            Style style = styleEntry.getValue();
            builder.append("  - ").append(styleEntry.getKey()).append(":\n");
            builder.append("      primaryColor: ").append(style.primaryColor).append("\n");
            builder.append("      secondaryColor: ").append(style.secondaryColor).append("\n");
            builder.append("      outlineColor: ").append(style.outlineColor).append("\n");
            builder.append("      backColor: ").append(style.backColor).append("\n");
        }
        return builder.toString();
    }

    /**
     * Class to represent a SubStation Alpha style.
     *
     * @since 1.0
     */
    public static class Style {
        /**
         * The name of the style.
         */
        public String name;

        /**
         * The font referenced by the style.
         */
        public Font font;

        /**
         * The primary colour of the subtitle.
         */
        public Color primaryColor;

        /**
         * The secondary colour of the subtitle.
         */
        public Color secondaryColor;

        /**
         * The colour to draw the outline of the subtitle.
         */
        public Color outlineColor;

        /**
         * The background colour to draw the subtitle on.
         */
        public Color backColor;
    }

    /**
     * An actual line of subtitle. Represents one line ONLY.
     *
     * @since 1.0
     */
    public static class RowInfo {
        /**
         * The subtitle text. Only ONE line.
         */
        public String text;

        /**
         * The font to draw the line in, taking into account resizing.
         */
        public Font font;

        /**
         * The other style attributes and the original font of the line.
         */
        public Style style;

        /**
         * The width of the line of text.
         */
        public int width;

        /**
         * The offset from the top of the video to draw on.
         */
        public int y;
    }

    /**
     * Helper to draw the subtitle.
     */
    public class DrawHelper {
        HashMap<String, Font> fontCache = new HashMap<String, Font>();
        double scale;
        int spacing = 5;
        public DonkeyParser parser;

        protected DrawHelper() {
            this.parser = DonkeyParser.this;
        }

        /**
         * Gets the currently set scaling factor.
         *
         * @return the current scale factor.
         */
        public double getScale() {
            return scale;
        }

        /**
         * Sets the currently set scaling factor.
         * <p/>
         * Does nothing if the scaling factor is not changed.
         *
         * @param scale The scale factor to change to.
         */
        public void setScale(double scale) {
            if (this.scale != scale) {
                this.scale = scale;
                fontCache.clear();
            }
        }

        /**
         * Gets the current line spacing to draw the subtitles.
         *
         * @return the current line spacing
         */
        public int getSpacing() {
            return spacing;
        }

        /**
         * Sets the current line spacing to draw the subtitles.
         *
         * @param spacing the line spacing to set to.
         */
        public void setSpacing(int spacing) {
            this.spacing = spacing;
        }

        /**
         * Helper to apply the scaling to the font in a style, with a cache.
         *
         * @param style the style whose font is to be scaled.
         * @return the scaled font.
         */
        public Font getFont(Style style) {
            Font font = fontCache.get(style.name);
            if (font != null)
                return font;
            font = style.font.deriveFont((float) (style.font.getSize() * scale));
            fontCache.put(style.name, font);
            return font;
        }

        /**
         * Start the drawing of a group of subtitles.
         *
         * @param graphics The {@link java.awt.Graphics} object to draw on.
         * @return The helper to draw a group of subtitles.
         */
        public Group draw(Graphics graphics) {
            return new Group(graphics);
        }

        /**
         * Represent a group of subtitles to be drawn, i.e. on the same screen.
         *
         * @since 1.0
         */
        public class Group {
            private final Graphics graphics;
            HashMap<String, FontMetrics> metricsCache = new HashMap<String, FontMetrics>();
            ArrayList<RowInfo> subtitles = new ArrayList<RowInfo>();

            protected Group(Graphics graphics) {
                this.graphics = graphics;
            }

            /**
             * Adds a subtitle to the group.
             *
             * @param subtitle The abstract representation of a SubStation Alpha dialog.
             */
            public void addSubtitle(DonkeySubtitle subtitle) {
                for (String line : subtitle.line.split("\\r?\\n")) {
                    RowInfo row = new RowInfo();
                    row.style = subtitle.style;
                    row.text = line;
                    subtitles.add(row);
                }
            }

            /**
             * Gets the font metrics of a SubStation Alpha style, as drawn on the current graphics object.
             * <p/>
             * Note: it's cached.
             *
             * @param style the SubStation Alpha style.
             * @return the font metrics.
             */
            public FontMetrics getMetrics(Style style) {
                FontMetrics metrics = metricsCache.get(style.name);
                if (metrics != null)
                    return metrics;
                metrics = graphics.getFontMetrics(getFont(style));
                metricsCache.put(style.name, metrics);
                return metrics;
            }

            /**
             * Gets the height of the current subtitle group.
             *
             * @return the height of the current subtitle group when drawn.
             */
            public int getHeight() {
                int height = 0;
                for (RowInfo row : subtitles) {
                    height += getMetrics(row.style).getHeight() + spacing;
                }
                return Math.max(0, height - spacing);
            }

            /**
             * Gets all the lines of subtitles in the current group.
             *
             * @return A collection of lines of subtitles.
             */
            public List<RowInfo> getRows() {
                int y = 0;

                for (RowInfo row : subtitles) {
                    FontMetrics metrics = getMetrics(row.style);
                    row.y = y;
                    row.font = getFont(row.style);
                    row.width = metrics.stringWidth(row.text);
                    y += metrics.getHeight() + spacing;
                }

                return Collections.unmodifiableList(subtitles);
            }
        }
    }

    /**
     * Helper function to access a {@link java.util.Map} with a default value.
     *
     * @param map          The map to access.
     * @param key          The key to retrieve.
     * @param defaultValue The default value to return if the key doesn't exist.
     * @param <K>          The type of keys in the map.
     * @param <V>          The type of values in the map.
     * @return The value of the key if exists, otherwise the default value.
     */
    public static <K, V> V getWithDefault(Map<K, V> map, K key, V defaultValue) {
        V ret = map.get(key);
        return ret != null ? ret : defaultValue;
    }
}
