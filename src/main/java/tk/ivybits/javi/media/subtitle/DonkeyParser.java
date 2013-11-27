package tk.ivybits.javi.media.subtitle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        public Font font;
        public Color primaryColor;
        public Color secondaryColor;
        public Color outlineColor;
        public Color backColor;
    }

    public static <K,V> V getWithDefault(Map<K, V> map, K key, V defaultValue) {
        V ret = map.get(key);
        if (ret == null) {
            return defaultValue;
        }
        return ret;
    }
}
