package dev.reloadx;

import dev.reloadx.commands.OtherFishingCommandExecutor;
import dev.reloadx.commands.TabComplete;
import dev.reloadx.listeners.FishingListener;
import dev.reloadx.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherFishing extends JavaPlugin {
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        configManager = new ConfigManager(this);

        registerListeners();
        registerCommands();

        getLogger().info("OtherFishing ha sido habilitado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OtherFishing ha sido deshabilitado.");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new FishingListener(this), this);
    }

    private void registerCommands() {
        getCommand("fishing").setExecutor(new OtherFishingCommandExecutor(this));
        getCommand("fishing").setTabCompleter(new TabComplete(this));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
