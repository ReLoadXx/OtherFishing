package dev.reloadx.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    public static String hex(String message) {
        Pattern pattern = Pattern.compile("(#[A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexCode = matcher.group();

            StringBuilder formattedHex = new StringBuilder("§x");
            for (char c : hexCode.substring(1).toCharArray()) {
                formattedHex.append("§").append(c);
            }

            message = message.replace(hexCode, formattedHex.toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
