package tk.ivybits.javi.media.subtitle;

import java.awt.*;
import java.util.*;

public class DonkeyParser {
    protected String version;
    protected ArrayList<String> format;
    protected HashMap<String, Style> styles = new HashMap<>();

    public DonkeyParser(String header) {
        for (String line : header.split("\\r?\\n")) {
            processLine(line);
        }
    }

    protected void processFormat(String data) {
        format = new ArrayList<String>(Arrays.asList(data.split("\\s*,\\s*")));
    }

    protected HashMap<String, String> parseWithFormat(String line) {
        HashMap<String, String> map = new HashMap<>();
        String[] data = line.split("\\s*,\\s*");
        int size = Math.min(format.size(), data.length);
        for (int i = 0; i < size; ++i) {
            map.put(format.get(i), data[i]);
        }
        return map;
    }

    protected int parseInt(String number) {
        if (number.startsWith("&H")) {
            return Integer.parseInt(number.substring(2), 16);
        } else {
            return Integer.parseInt(number);
        }
    }

    protected void processStyle(String line) {
        HashMap<String, String> map = parseWithFormat(line);
        Style style = new Style();
        String name = map.get("Name");
        int fontStyle = 0;
        if (parseInt(getWithDefault(map, "Bold", "0")) != 0)
            fontStyle &= Font.BOLD;
        if (parseInt(getWithDefault(map, "Italic", "0")) != 0)
            fontStyle &= Font.ITALIC;
        Font font = new Font(getWithDefault(map, "Fontname", Font.SANS_SERIF), fontStyle,
                parseInt(getWithDefault(map, "Fontsize", "16")));
        style.name = name;
        style.font = font;
        style.primaryColor = new Color(parseInt(getWithDefault(map, "PrimaryColour", "&HFFFFFFFF")));
        style.secondaryColor = new Color(parseInt(getWithDefault(map, "SecondaryColour", "0")));
        style.outlineColor = new Color(parseInt(getWithDefault(map, "OutlineColour", "0")));
        style.backColor = new Color(parseInt(getWithDefault(map, "BackColour", "0")));
        styles.put(name, style);
    }

    protected void processLine(String line) {
        if (line.isEmpty() || line.startsWith("["))
            return;
        int colon = line.indexOf(":");
        if (colon == -1)
            return;
        String command = line.substring(0, colon);
        String data = line.substring(colon + 1).trim();
        //System.out.println("Parsed: Command: " + command + ", Data: " + data);
        switch (command) {
            case "ScriptType":
                version = data;
                break;
            case "Format":
                processFormat(data);
                break;
            case "Style":
                processStyle(data);
                break;
        }
    }

    protected long parseTimeStamp(String ts) {
        String[] time = ts.split(":");
        return Integer.parseInt(time[0]) * 3600_000 + Integer.parseInt(time[1]) * 60_000 + (int) (Double.parseDouble(time[2]) * 1000);
    }

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

    public DrawHelper getDrawHelper() {
        return new DrawHelper();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Version: " + version + "\n");
        builder.append("Styles:\n");
        for (Map.Entry<String, Style> styleEntry : styles.entrySet()) {
            Style style = styleEntry.getValue();
            builder.append("  - " + styleEntry.getKey() + ":\n");
            builder.append("      primaryColor: " + style.primaryColor + "\n");
            builder.append("      secondaryColor: " + style.secondaryColor + "\n");
            builder.append("      outlineColor: " + style.outlineColor + "\n");
            builder.append("      backColor: " + style.backColor + "\n");
        }
        return builder.toString();
    }

    public class Style {
        public String name;
        public Font font;
        public Color primaryColor;
        public Color secondaryColor;
        public Color outlineColor;
        public Color backColor;
    }

    public static class RowInfo {
        public String text;
        public Font font;
        public Style style;
        public int width;
        public int y;
    }

    public class DrawHelper {
        HashMap<String, Font> fontCache = new HashMap<>();
        double scale;
        int spacing = 5;
        public DonkeyParser parser;

        public DrawHelper() {
            this.parser = DonkeyParser.this;
        }

        public double getScale() {
            return scale;
        }

        public void setScale(double scale) {
            if (this.scale != scale) {
                this.scale = scale;
                fontCache.clear();
            }
        }

        public int getSpacing() {
            return spacing;
        }

        public void setSpacing(int spacing) {
            this.spacing = spacing;
        }

        public Font getFont(Style style) {
            Font font = fontCache.get(style.name);
            if (font != null)
                return font;
            font = style.font.deriveFont((float) (style.font.getSize() * scale));
            fontCache.put(style.name, font);
            return font;
        }

        public Group draw(Graphics graphics) {
            return new Group(graphics);
        }

        public class Group {
            private final Graphics graphics;
            HashMap<String, FontMetrics> metricsCache = new HashMap<>();
            ArrayList<RowInfo> subtitles = new ArrayList<>();

            public Group(Graphics graphics) {
                this.graphics = graphics;
            }

            public void addSubtitle(DonkeySubtitle subtitle) {
                for (String line : subtitle.line.split("\\r?\\n")) {
                    RowInfo row = new RowInfo();
                    row.style = subtitle.style;
                    row.text = line;
                    subtitles.add(row);
                }
            }

            public FontMetrics getMetrics(Style style) {
                FontMetrics metrics = metricsCache.get(style.name);
                if (metrics != null)
                    return metrics;
                metrics = graphics.getFontMetrics(getFont(style));
                metricsCache.put(style.name, metrics);
                return metrics;
            }

            public int getHeight() {
                int height = 0;
                for (RowInfo row : subtitles) {
                    height += getMetrics(row.style).getHeight() + spacing;
                }
                return Math.max(0, height - spacing);
            }

            public Collection<RowInfo> getRows() {
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

    public static <K, V> V getWithDefault(Map<K, V> map, K key, V defaultValue) {
        V ret = map.get(key);
        if (ret == null) {
            return defaultValue;
        }
        return ret;
    }
}
