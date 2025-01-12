package dev.reloadx.commands;

import dev.reloadx.OtherFishing;
import dev.reloadx.utils.ColorUtils;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;



public class OtherFishingCommandExecutor implements CommandExecutor {

    private final OtherFishing plugin;

    public OtherFishingCommandExecutor(OtherFishing plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fishing")) {
            if (args.length == 0) {
                String message = plugin.getConfigManager().getMessage("fishing.command_usage");
                message = MessageUtils.replacePlaceholders(message, null);
                sender.sendMessage(message);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("otherfishing.reload")) {
                    plugin.reloadConfig();
                    String message = plugin.getConfigManager().getMessage("fishing.reload_success");
                    message = MessageUtils.replacePlaceholders(message, null);
                    sender.sendMessage(message);
                    plugin.getLogger().info("La configuración ha sido recargada por " + sender.getName());
                    return true;
                } else {
                    String message = plugin.getConfigManager().getMessage("global.no_permission");
                    message = MessageUtils.replacePlaceholders(message, null);
                    sender.sendMessage(message);
                    return true;
                }
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                String rodType = args[1].toLowerCase();
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("otherfishing.give." + rodType)) {
                        giveSpecialRod(player, rodType);
                        return true;
                    } else {
                        String message = plugin.getConfigManager().getMessage("global.no_permission");
                        message = MessageUtils.replacePlaceholders(message, null);
                        player.sendMessage(message);
                        return true;
                    }
                } else {
                    String message = plugin.getConfigManager().getMessage("fishing.command_usage");
                    message = MessageUtils.replacePlaceholders(message, null);
                    sender.sendMessage(message);
                    return true;
                }
            }

        }
        return false;
    }

    private void giveSpecialRod(Player player, String rodType) {
        ConfigurationSection rodsSection = plugin.getConfig().getConfigurationSection("special_fishing_rods");

        if (rodsSection != null && rodsSection.contains(rodType)) {
            ConfigurationSection rodConfig = rodsSection.getConfigurationSection(rodType);

            if (rodConfig != null && rodConfig.contains("display_name")) {
                String rodDisplayName = rodConfig.getString("display_name");
                rodDisplayName = ColorUtils.hex(rodDisplayName);

                ItemStack rod = new ItemStack(Material.FISHING_ROD);
                ItemMeta rodMeta = rod.getItemMeta();
                if (rodMeta != null) {
                    rodMeta.setDisplayName(rodDisplayName);
                    rod.setItemMeta(rodMeta);
                }

                player.getInventory().addItem(rod);

                String message = plugin.getConfigManager().getMessage("fishing_event.rod_received");
                message = message.replace("%rod%", rodDisplayName);
                message = MessageUtils.replacePlaceholders(message, null);
                player.sendMessage(message);
            } else {
                String message = plugin.getConfigManager().getMessage("fishing_event.invalid_rod_config");
                message = MessageUtils.replacePlaceholders(message, null);
                player.sendMessage(message);
            }
        } else {
            String message = plugin.getConfigManager().getMessage("fishing_event.rod_not_found");
            message = message.replace("%rod%", rodType);
            message = MessageUtils.replacePlaceholders(message, null);
            player.sendMessage(message);
        }
    }
}
