package dev.cuteecactus.config;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

public class ProfileConfig {
    private final FileConfiguration config;
    private final UUID uuid;

    public ProfileConfig( UUID uuid) {
        this.config = ConfigManager.get().loadProfile("profile/"+uuid);
        this.uuid = uuid;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean save(FileConfiguration config) {
        return ConfigManager.get().save(config, "profile/"+uuid);
    }
}
