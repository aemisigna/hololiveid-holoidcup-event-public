package com.covercorp.holosports.commons.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommonUtil {
    public static String colorize(String string) {
        final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            String color = string.substring(matcher.start(), matcher.end());
            string = string.replace(color, String.valueOf(ChatColor.of(color)));
            matcher = pattern.matcher(string);
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String capitalizeString(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
