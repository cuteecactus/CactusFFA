package me.cactusffa.service;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.Arena;
import me.cactusffa.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ArenaManager {

    private final CactusFFAPlugin plugin;
    private final Map<String, Arena> arenas = new LinkedHashMap<>();
    private YamlConfiguration config;
    private File file;

    public ArenaManager(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        this.file = new File(plugin.getDataFolder(), "arenas.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.arenas.clear();
        ConfigurationSection root = config.getConfigurationSection("arenas");
        if (root == null) {
            return;
        }
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            Location spawn = LocationUtil.fromSection(section.getConfigurationSection("spawn"));
            if (spawn == null) {
                continue;
            }
            String displayName = section.getString("display-name", id);
            arenas.put(id.toLowerCase(), new Arena(id.toLowerCase(), displayName, spawn));
        }
    }

    public Optional<Arena> find(String id) {
        return Optional.ofNullable(arenas.get(id.toLowerCase()));
    }

    public Collection<Arena> all() {
        return Collections.unmodifiableCollection(arenas.values());
    }

    public void saveArena(String id, Location spawn) {
        String key = id.toLowerCase();
        ConfigurationSection root = config.getConfigurationSection("arenas");
        if (root == null) {
            root = config.createSection("arenas");
        }
        ConfigurationSection section = root.getConfigurationSection(key);
        if (section == null) {
            section = root.createSection(key);
        }
        section.set("display-name", id);
        ConfigurationSection spawnSection = section.getConfigurationSection("spawn");
        if (spawnSection == null) {
            spawnSection = section.createSection("spawn");
        }
        LocationUtil.write(spawnSection, spawn);
        arenas.put(key, new Arena(key, id, spawn));
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save arenas.yml: " + exception.getMessage());
        }
    }
}
