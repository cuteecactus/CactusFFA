package dev.cuteecactus.config;

import org.bukkit.configuration.file.FileConfiguration;

public class BaseConfig {
    private FileConfiguration config;

    private static BaseConfig instance;

    public BaseConfig() {
        this.config = ConfigManager.get().load("config");
        instance = this;
    }

    public static BaseConfig get () {
        return instance;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        this.config = ConfigManager.get().load("config");
    }

    public boolean save(FileConfiguration config) {
        return ConfigManager.get().save(config, "config");
    }
}
