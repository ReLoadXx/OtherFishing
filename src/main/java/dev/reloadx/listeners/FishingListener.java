package dev.reloadx.listeners;

import dev.reloadx.OtherFishing;
import dev.reloadx.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class FishingListener implements Listener {
    private final OtherFishing plugin;

    public FishingListener(OtherFishing plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() == null) return;

        Player player = event.getPlayer();
        ItemStack fishingRod = player.getInventory().getItemInMainHand();

        if (fishingRod.getType() == Material.FISHING_ROD && fishingRod.hasItemMeta()) {
            String rodName = fishingRod.getItemMeta().getDisplayName();

            rodName = "§x" + ColorUtils.hex(rodName);
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
                            handleSpecialFishing(event, player, rodConfig);
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void handleSpecialFishing(PlayerFishEvent event, Player player, ConfigurationSection rodConfig) {
        List<Map<?, ?>> drops = rodConfig.getMapList("drops");
        Random random = new Random();

        double totalProbability = 0;
        for (Map<?, ?> dropConfig : drops) {
            totalProbability += (double) dropConfig.get("chance");
        }

        double randomValue = random.nextDouble() * totalProbability;
        double accumulatedProbability = 0;

        for (Map<?, ?> dropConfig : drops) {
            double chance = (double) dropConfig.get("chance");
            accumulatedProbability += chance;

            if (randomValue <= accumulatedProbability) {
                Material material = Material.matchMaterial((String) dropConfig.get("item"));
                if (material != null) {
                    int quantity = dropConfig.containsKey("quantity") ? (int) dropConfig.get("quantity") : 1;

                    ItemStack item = new ItemStack(material, quantity);
                    ItemMeta meta = item.getItemMeta();

                    if (meta != null) {
                        if (dropConfig.containsKey("display_name")) {
                            String displayName = (String) dropConfig.get("display_name");
                            displayName = ColorUtils.hex(displayName);
                            meta.setDisplayName(displayName);
                        }

                        if (dropConfig.containsKey("lore")) {
                            List<String> lore = (List<String>) dropConfig.get("lore");
                            lore = lore.stream().map(ColorUtils::hex).toList();
                            meta.setLore(lore);
                        }

                        if (dropConfig.containsKey("enchantments")) {
                            List<String> enchantments = (List<String>) dropConfig.get("enchantments");
                            for (String enchantment : enchantments) {
                                String[] parts = enchantment.split(":");
                                if (parts.length == 2) {
                                    Enchantment enchant = Enchantment.getByName(parts[0]);
                                    int level = Integer.parseInt(parts[1]);
                                    if (enchant != null) {
                                        meta.addEnchant(enchant, level, true);
                                    }
                                }
                            }
                        }

                        item.setItemMeta(meta);
                    }

                    player.getWorld().dropItemNaturally(player.getLocation(), item);

                    if (dropConfig.containsKey("particles")) {
                        Map<String, Object> particlesConfig = (Map<String, Object>) dropConfig.get("particles");
                        String particleType = (String) particlesConfig.get("type");
                        int amount = (int) particlesConfig.get("amount");

                        player.getWorld().spawnParticle(
                                org.bukkit.Particle.valueOf(particleType.toUpperCase()),
                                player.getLocation(),
                                amount
                        );
                    }

                    if (dropConfig.containsKey("title")) {
                        Map<String, Object> titleConfig = (Map<String, Object>) dropConfig.get("title");
                        String text = ColorUtils.hex((String) titleConfig.get("text"));
                        String subtitle = ColorUtils.hex((String) titleConfig.get("subtitle"));

                        player.sendTitle(text, subtitle, 10, 70, 20);
                    }

                    if (dropConfig.containsKey("sound")) {
                        Map<String, Object> soundConfig = (Map<String, Object>) dropConfig.get("sound");
                        String soundType = (String) soundConfig.get("type");
                        float volume = ((Double) soundConfig.get("volume")).floatValue();
                        float pitch = ((Double) soundConfig.get("pitch")).floatValue();

                        player.getWorld().playSound(
                                player.getLocation(),
                                org.bukkit.Sound.valueOf(soundType.toUpperCase()),
                                volume,
                                pitch
                        );
                    }

                    break;
                }
            }
        }

        event.getHook().remove();
    }


}

