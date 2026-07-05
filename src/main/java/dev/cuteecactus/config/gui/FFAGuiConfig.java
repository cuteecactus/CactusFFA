package dev.cuteecactus.config.gui;

import org.bukkit.configuration.file.FileConfiguration;

import dev.cuteecactus.config.ConfigManager;

public class FFAGuiConfig {
    private FileConfiguration config;

    private static FFAGuiConfig instance;

    public FFAGuiConfig() {
        this.config = ConfigManager.get().load("guis/ffa.yml");
        instance = this;
    }

    public static FFAGuiConfig get () {
        return instance;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        this.config = ConfigManager.get().load("guis/ffa.yml");
    }

    public boolean save(FileConfiguration config) {
        return ConfigManager.get().save(config, "guis/ffa.yml");
    }
}
