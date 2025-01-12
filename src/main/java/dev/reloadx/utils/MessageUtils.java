package dev.reloadx.utils;

import dev.reloadx.OtherFishing;
import java.util.Map;

public class MessageUtils {

    private static final OtherFishing plugin = OtherFishing.getPlugin(OtherFishing.class);

    public static String replacePlaceholders(String message, Map<String, String> placeholders) {
        if (message == null) return "";

        String prefix = plugin.getConfigManager().getMessage("prefix");
        message = message.replace("%prefix%", prefix);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return ColorUtils.hex(message);
    }
}

