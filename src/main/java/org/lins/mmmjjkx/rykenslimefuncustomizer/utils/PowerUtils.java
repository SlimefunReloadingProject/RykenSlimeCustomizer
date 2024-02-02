package org.lins.mmmjjkx.rykenslimefuncustomizer.utils;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PowerUtils {
    private static final DecimalFormat powerFormat = new DecimalFormat("###,###.##",
        DecimalFormatSymbols.getInstance(Locale.ROOT));

    public static String powerBuffer(double power) {
        return power(power, " 可存储");
    }

    public static String powerPerSecond(double power) {
        return power(perTickToPerSecond(power), "/s");
    }

    public static String powerPerTick(double power) {
        return power(power, "/t");
    }

    public static String power(double power, String suffix) {
        return "&8⇨ &e⚡ &7" + powerFormatAndFadeDecimals(power) + " J" + suffix;
    }


    public static String powerFormatAndFadeDecimals(double power) {
        String formattedString = powerFormat.format(power);
        if (formattedString.indexOf('.') != -1) {
            return formattedString.substring(0, formattedString.indexOf('.')) + ChatColor.DARK_GRAY
                    + formattedString.substring(formattedString.indexOf('.')) + ChatColor.GRAY;
        } else {
            return formattedString;
        }
    }

    public static double perTickToPerSecond(double power) {
        if (Constants.CUSTOM_TICKER_DELAY <= 0) {
            return (Constants.SERVER_TICK_RATE * power);
        } else {
            return (1 / ((double) Constants.CUSTOM_TICKER_DELAY / Constants.SERVER_TICK_RATE) * power);
        }
    }
}
