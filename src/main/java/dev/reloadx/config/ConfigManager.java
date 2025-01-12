package dev.reloadx.config;

import dev.reloadx.OtherFishing;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ConfigManager {
    private final OtherFishing plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public ConfigManager(OtherFishing plugin) {
        this.plugin = plugin;
        reloadMessagesConfig();
    }

    public void reloadMessagesConfig() {
        if (plugin.getDataFolder() != null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String key) {
        String defaultNotFound = messagesConfig.getString("global.no_found", "Mensaje no encontrado.");
        return messagesConfig.getString(key, defaultNotFound);
    }
}


