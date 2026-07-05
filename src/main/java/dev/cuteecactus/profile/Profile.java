package dev.cuteecactus.profile;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import dev.cuteecactus.config.ProfileConfig;

public class Profile {
    private final UUID uuid;
    private final FileConfiguration config;
    private ProfileState profileState = ProfileState.IN_LOBBY;

    public Profile (UUID uuid) {
        this.uuid = uuid;
        this.config = new ProfileConfig(uuid).getConfig();
    }

    public UUID getUuid() {
        return uuid;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setProfileState(ProfileState profileState) {
        this.profileState = profileState;
    }

    public ProfileState getProfileState() {
        return profileState;
    }
}
