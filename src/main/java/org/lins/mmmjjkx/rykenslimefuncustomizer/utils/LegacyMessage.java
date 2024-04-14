package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyMessage {
    private static final Map<String, String> REPLACES;
    private static final Pattern RGB_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}|#[0-9a-fA-F]{3}");

    static {
        REPLACES = new HashMap<>();

        REPLACES.put("0", "<black>");
        REPLACES.put("1", "<dark_blue>");
        REPLACES.put("2", "<dark_green>");
        REPLACES.put("3", "<dark_aqua>");
        REPLACES.put("4", "<dark_red>");
        REPLACES.put("5", "<dark_purple>");
        REPLACES.put("6", "<gold>");
        REPLACES.put("7", "<gray>");
        REPLACES.put("8", "<dark_gray>");
        REPLACES.put("9", "<blue>");
        REPLACES.put("a", "<green>");
        REPLACES.put("b", "<aqua>");
        REPLACES.put("c", "<red>");
        REPLACES.put("d", "<light_purple>");
        REPLACES.put("e", "<yellow>");
        REPLACES.put("f", "<white>");
        REPLACES.put("k", "<magic>");
        REPLACES.put("l", "<bold>");
        REPLACES.put("m", "<strikethrough>");
        REPLACES.put("n", "<underline>");
        REPLACES.put("o", "<italic>");
        REPLACES.put("r", "<reset>");
    }

    /**
     * Converts legacy message to MiniMessage
     *
     * @param legacy Legacy message
     * @param character Character used for legacy formatting
     * @return MiniMessage-compatible format
     */
    public static String fromLegacy(String legacy, String character) {
        for (Map.Entry<String, String> entry : REPLACES.entrySet()) {
            legacy = legacy.replace(character + entry.getKey(), entry.getValue());
        }

        for (Matcher matcher = RGB_PATTERN.matcher(legacy); matcher.find(); matcher = RGB_PATTERN.matcher(legacy)) {
            String color = matcher.group();
            String hex = color.substring(1);
            if (hex.length() == 3) {
                hex = String.valueOf(hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2));
            } else if (hex.length() == 6) {
                hex = String.valueOf(hex.charAt(0) + hex.charAt(1) + hex.charAt(2) + hex.charAt(3) + hex.charAt(4) + hex.charAt(5));
            }
            String replacement = "<#" + hex + ">";
            legacy = legacy.replace(color, replacement);
        }

        return legacy;
    }
}