package dev.cuteecactus.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cuteecactus.CactusFFA;

public class ConfigManager {
    private final JavaPlugin plugin = CactusFFA.get();
    private static ConfigManager instance;

    public ConfigManager() {
        instance = this;
        init();
    }

    public static ConfigManager get() {
        return instance;
    }

    private void init() {
        new KitsConfig();
        new MessageConfig();
    } 

    public FileConfiguration load(String name) {
        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public boolean save(FileConfiguration config, String name) {
        try {
            config.save(new File(plugin.getDataFolder(), name + ".yml"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
