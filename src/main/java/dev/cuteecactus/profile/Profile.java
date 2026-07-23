package dev.cuteecactus.profile;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import dev.cuteecactus.arena.Arena;
import dev.cuteecactus.config.ProfileConfig;
import dev.cuteecactus.kits.Kit;

public class Profile {
    private final UUID uuid;
    private final FileConfiguration config;
    private ProfileState profileState = ProfileState.IN_LOBBY;
    private Kit currentKit;
    private Arena currentArena;

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

    public Kit getCurrentKit() {
        return currentKit;
    }
    public void setCurrentKit(Kit currentKit) {
        this.currentKit = currentKit;
    }

    public Arena getCurrentArena() {
        return currentArena;
    }

    public void setCurrentArena(Arena currentArena) {
        this.currentArena = currentArena;
    }
}
