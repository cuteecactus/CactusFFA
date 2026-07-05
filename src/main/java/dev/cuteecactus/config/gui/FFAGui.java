package dev.cuteecactus.config.gui;

import org.bukkit.configuration.file.FileConfiguration;

import dev.cuteecactus.config.ConfigManager;

public class FFAGui {
    private FileConfiguration config;

    private static FFAGui instance;

    public FFAGui() {
        this.config = ConfigManager.get().load("guis/ffa.yml");
        instance = this;
    }

    public static FFAGui get () {
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
