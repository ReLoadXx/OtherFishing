package dev.reloadx.handlers;

import dev.reloadx.utils.ColorUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpecialFishingHandler {

    public void handle(PlayerFishEvent event, Player player, ConfigurationSection rodConfig) {
        List<Map<?, ?>> drops = rodConfig.getMapList("drops");
        Random random = new Random();

        double totalProbability = drops.stream()
                .mapToDouble(dropConfig -> (double) dropConfig.get("chance"))
                .sum();

        double randomValue = random.nextDouble() * totalProbability;
        double accumulatedProbability = 0;

        for (Map<?, ?> dropConfig : drops) {
            accumulatedProbability += (double) dropConfig.get("chance");

            if (randomValue <= accumulatedProbability) {
                processDrop(player, dropConfig);
                break;
            }
        }

        event.getHook().remove();
    }

    private void processDrop(Player player, Map<?, ?> dropConfig) {
        Material material = Material.matchMaterial((String) dropConfig.get("item"));
        if (material == null) return;

        int quantity = dropConfig.containsKey("quantity") ? (int) dropConfig.get("quantity") : 1;

        ItemStack item = new ItemStack(material, quantity);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (dropConfig.containsKey("display_name")) {
                meta.setDisplayName(ColorUtils.hex((String) dropConfig.get("display_name")));
            }

            if (dropConfig.containsKey("lore")) {
                List<String> lore = (List<String>) dropConfig.get("lore");
                meta.setLore(lore.stream().map(ColorUtils::hex).toList());
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
            playParticles(player, (Map<String, Object>) dropConfig.get("particles"));
        }

        if (dropConfig.containsKey("title")) {
            sendTitle(player, (Map<String, Object>) dropConfig.get("title"));
        }

        if (dropConfig.containsKey("sound")) {
            playSound(player, (Map<String, Object>) dropConfig.get("sound"));
        }
    }

    private void playParticles(Player player, Map<String, Object> particlesConfig) {
        String particleType = (String) particlesConfig.get("type");
        int amount = (int) particlesConfig.get("amount");

        player.getWorld().spawnParticle(
                Particle.valueOf(particleType.toUpperCase()),
                player.getLocation(),
                amount
        );
    }

    private void sendTitle(Player player, Map<String, Object> titleConfig) {
        String text = ColorUtils.hex((String) titleConfig.get("text"));
        String subtitle = ColorUtils.hex((String) titleConfig.get("subtitle"));

        player.sendTitle(text, subtitle, 10, 70, 20);
    }

    private void playSound(Player player, Map<String, Object> soundConfig) {
        String soundType = (String) soundConfig.get("type");
        float volume = ((Double) soundConfig.get("volume")).floatValue();
        float pitch = ((Double) soundConfig.get("pitch")).floatValue();

        player.getWorld().playSound(
                player.getLocation(),
                Sound.valueOf(soundType.toUpperCase()),
                volume,
                pitch
        );
    }
}
