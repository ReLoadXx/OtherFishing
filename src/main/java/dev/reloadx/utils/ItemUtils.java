package dev.reloadx.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;
import java.util.List;

public class ItemUtils {

    public static void applyItemProperties(ItemStack item, ConfigurationSection rodConfig) {
        if (item != null && rodConfig != null) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                if (rodConfig.contains("display_name")) {
                    String displayName = rodConfig.getString("display_name");
                    displayName = ColorUtils.hex(displayName);
                    meta.setDisplayName(displayName);
                }

                if (rodConfig.contains("lore")) {
                    List<String> lore = rodConfig.getStringList("lore");
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i, ColorUtils.hex(lore.get(i)));
                    }
                    meta.setLore(lore);
                }

                item.setItemMeta(meta);
            }
        }
    }
}

