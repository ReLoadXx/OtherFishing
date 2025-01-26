package dev.reloadx.listeners;

import dev.reloadx.OtherFishing;
import dev.reloadx.handlers.SpecialFishingHandler;
import dev.reloadx.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishingListener implements Listener {
    private final OtherFishing plugin;
    private final SpecialFishingHandler specialFishingHandler;

    public FishingListener(OtherFishing plugin) {
        this.plugin = plugin;
        this.specialFishingHandler = new SpecialFishingHandler();
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() == null) return;

        Player player = event.getPlayer();
        ItemStack fishingRod = player.getInventory().getItemInMainHand();

        if (fishingRod.getType() == Material.FISHING_ROD && fishingRod.hasItemMeta()) {
            String rodName = fishingRod.getItemMeta().getDisplayName();

            rodName = ColorUtils.hex(rodName);
            ConfigurationSection rodsSection = plugin.getConfig().getConfigurationSection("special_fishing_rods");

            if (rodsSection != null) {
                for (String key : rodsSection.getKeys(false)) {
                    ConfigurationSection rodConfig = rodsSection.getConfigurationSection(key);
                    if (rodConfig != null) {
                        String configRodName = rodConfig.getString("display_name");

                        String convertedConfigRodName = ColorUtils.hex(configRodName);
                        convertedConfigRodName = convertedConfigRodName.toUpperCase();
                        rodName = rodName.toUpperCase();

                        if (rodName.equals(convertedConfigRodName)) {
                            specialFishingHandler.handle(event, player, rodConfig);
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }
}
