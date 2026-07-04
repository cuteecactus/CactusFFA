package dev.cuteecactus.service;

import dev.cuteecactus.CactusFFAPlugin;
import dev.cuteecactus.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public final class WorldService {

    private final CactusFFAPlugin plugin;

    public WorldService(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    public Location fallbackLeaveLocation() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ffa.leave.fallback-location");
        return LocationUtil.fromSection(section);
    }

    public Location mainLobbyLocation() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ffa.leave.main-lobby");
        if (section == null || !section.getBoolean("enabled", false)) {
            return null;
        }
        return LocationUtil.fromSection(section);
    }

    public void setMainLobby(Location location) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ffa.leave.main-lobby");
        if (section == null) {
            section = plugin.getConfig().createSection("ffa.leave.main-lobby");
        }
        section.set("enabled", true);
        LocationUtil.write(section, location);
        plugin.saveConfig();
        plugin.reloadConfig();
    }
}
