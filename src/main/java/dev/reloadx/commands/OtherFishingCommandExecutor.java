package dev.reloadx.commands;

import dev.reloadx.OtherFishing;
import dev.reloadx.utils.ItemUtils;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
                    plugin.getConfigManager().reloadMessagesConfig();

                    String message = plugin.getConfigManager().getMessage("fishing.reload_success");
                    message = MessageUtils.replacePlaceholders(message, null);
                    sender.sendMessage(message);
                    plugin.getLogger().info("La configuración y los mensajes han sido recargados por " + sender.getName());
                    return true;
                } else {
                    String message = plugin.getConfigManager().getMessage("global.no_permission");
                    message = MessageUtils.replacePlaceholders(message, null);
                    sender.sendMessage(message);
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("help")) {
                showHelp(sender);
                return true;
            }

            if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                String playerName = args[1];
                String rodType = args[2].toLowerCase();

                if (!sender.hasPermission("otherfishing.give." + rodType)) {
                    String message = plugin.getConfigManager().getMessage("global.no_permission");
                    message = MessageUtils.replacePlaceholders(message, null);
                    sender.sendMessage(message);
                    return true;
                }

                Player targetPlayer = plugin.getServer().getPlayer(playerName);
                if (targetPlayer == null) {
                    String message = plugin.getConfigManager().getMessage("fishing_event.rod_not_online");
                    message = message.replace("%player%", playerName);
                    message = MessageUtils.replacePlaceholders(message, null);
                    sender.sendMessage(message);
                    return true;
                }

                giveSpecialRod(targetPlayer, rodType, sender);
                return true;
            }
        }
        return false;
    }

    private void showHelp(CommandSender sender) {
        String helpMessage = plugin.getConfigManager().getMessage("fishing.help_header");
        String reloadCommand = plugin.getConfigManager().getMessage("global.command_help.reload");
        String giveCommand = plugin.getConfigManager().getMessage("global.command_help.give");
        String helpCommand = plugin.getConfigManager().getMessage("global.command_help.help");
        helpMessage = MessageUtils.replacePlaceholders(helpMessage, null);
        sender.sendMessage(helpMessage);
        sender.sendMessage(reloadCommand);
        sender.sendMessage(giveCommand);
        sender.sendMessage(helpCommand);


        String helpFooter = plugin.getConfigManager().getMessage("fishing.help_footer");
        helpFooter = MessageUtils.replacePlaceholders(helpFooter, null);
        sender.sendMessage(helpFooter);
    }

    private void giveSpecialRod(Player player, String rodType, CommandSender sender) {
        ConfigurationSection rodsSection = plugin.getConfig().getConfigurationSection("special_fishing_rods");

        if (rodsSection != null && rodsSection.contains(rodType)) {
            ConfigurationSection rodConfig = rodsSection.getConfigurationSection(rodType);

            if (rodConfig != null) {
                ItemStack rod = new ItemStack(Material.FISHING_ROD);

                ItemUtils.applyItemProperties(rod, rodConfig);

                player.getInventory().addItem(rod);

                String message = plugin.getConfigManager().getMessage("fishing_event.rod_received");
                message = message.replace("%rod%", rod.getItemMeta().getDisplayName());
                message = MessageUtils.replacePlaceholders(message, null);
                player.sendMessage(message);

                message = plugin.getConfigManager().getMessage("fishing_event.rod_given");
                message = message.replace("%player%", player.getName());
                message = message.replace("%rod%", rod.getItemMeta().getDisplayName());
                message = MessageUtils.replacePlaceholders(message, null);
                sender.sendMessage(message);
            } else {
                String message = plugin.getConfigManager().getMessage("fishing_event.invalid_rod_config");
                message = MessageUtils.replacePlaceholders(message, null);
                sender.sendMessage(message);
            }
        } else {
            String message = plugin.getConfigManager().getMessage("fishing_event.rod_not_found");
            message = message.replace("%rod%", rodType);
            message = MessageUtils.replacePlaceholders(message, null);
            sender.sendMessage(message);
        }
    }
}
