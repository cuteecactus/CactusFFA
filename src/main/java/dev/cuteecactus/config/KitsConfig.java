package dev.cuteecactus.config;

import org.bukkit.configuration.file.FileConfiguration;

public class KitsConfig {
    private FileConfiguration config;

    private static KitsConfig instance;

    public KitsConfig() {
        this.config = ConfigManager.get().load("kits");
        instance = this;
    }

    public static KitsConfig get () {
        return instance;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        this.config = ConfigManager.get().load("kits");
    }

    public boolean save(FileConfiguration config) {
        return ConfigManager.get().save(config, "kits");
    }
}
