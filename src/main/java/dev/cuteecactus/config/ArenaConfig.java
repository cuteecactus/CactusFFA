package dev.cuteecactus.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ArenaConfig {
    private FileConfiguration config;

    private static ArenaConfig instance;

    public ArenaConfig() {
        this.config = ConfigManager.get().load("arenas");
        instance = this;
    }

    public static ArenaConfig get () {
        return instance;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        this.config = ConfigManager.get().load("arenas");
    }

    public boolean save(FileConfiguration config) {
        return ConfigManager.get().save(config, "arenas");
    }
}
