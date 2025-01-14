package dev.reloadx.commands;

import dev.reloadx.OtherFishing;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    private final OtherFishing plugin;

    public TabComplete(OtherFishing plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
            if ("give".startsWith(args[0].toLowerCase())) {
                completions.add("give");
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            ConfigurationSection rodsSection = plugin.getConfig().getConfigurationSection("special_fishing_rods");
            if (rodsSection != null) {
                for (String key : rodsSection.getKeys(false)) {
                    if (key.startsWith(args[2].toLowerCase())) {
                        completions.add(key);
                    }
                }
            }
        }

        return completions;
    }
}
