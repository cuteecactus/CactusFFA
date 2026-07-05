package dev.cuteecactus.arena;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import dev.cuteecactus.config.ArenaConfig;

public class ArenaManager {
    private static ArenaManager instance;

    private FileConfiguration config = new ArenaConfig().getConfig();

    private final ConcurrentHashMap<String, Arena> arenas = new ConcurrentHashMap<>();

    public ArenaManager() {
        instance = this;
        loadArenas();
    }

    public static ArenaManager get() {
        return instance;
    }

    private void loadArenas() {
        for (String id : config.getConfigurationSection("arenas").getKeys(false)) {
            String path = "arenas." + id;

            if (!config.contains(path))
                continue;

            path = path + ".";
            Arena arena = new Arena(id);
            arena.setName(config.getString(path + "name"));
            arena.setCorner1(config.getLocation(path + "corner1"));
            arena.setCorner2(config.getLocation(path + "corner2"));
            arena.setSpawn(config.getLocation(path + "spawn"));
            arena.setEnabled(config.getBoolean(path + "enabled"));

            arenas.put(id.toLowerCase(), arena);
        }
    }

    public boolean createArena(String id) {
        if (arenas.contains(id)) {
            return false;
        }

        Arena arena = new Arena(id);

        arena.setName(id);
        arena.setCorner1(null);
        arena.setCorner2(null);
        arena.setSpawn(null);
        arena.setEnabled(false);

        String path = "arenas." + id + ".";

        config.set(path + "name", arena.getName());
        config.set(path + "enabled", arena.isEnabled());
        config.set(path + "spawn", arena.getSpawn());
        config.set(path + "corner1", arena.getCorner1());
        config.set(path + "corner2", arena.getCorner2());

        ArenaConfig.get().save(config);

        arenas.put(arena.getId().toLowerCase(), arena);

        return true;
    }

    public Arena getArena(String id) {
        return arenas.getOrDefault(id, null);
    }

    public boolean rename(String id, String name) {
        if (!arenas.contains(id))
            return false;

        Arena arena = getArena(id);

        if (arena == null)
            return false;

        arena.setName(name);
        config.set("arenas." + id + ".name", name);
        ArenaConfig.get().save(config);
        arenas.put(id, arena);

        return true;
    }

    public boolean setSpawn(String id, Location location) {
        Arena arena = getArena(id);

        if (arena == null)
            return false;

        arena.setSpawn(location);
        config.set("arenas." + id + ".spawn", location);
        ArenaConfig.get().save(config);
        arenas.put(id, arena);

        return true;
    }

    public boolean setCorner1(String id, Location location) {
        Arena arena = getArena(id);

        if (arena == null)
            return false;

        arena.setCorner1(location);
        config.set("arenas." + id + ".corner1", location);
        ArenaConfig.get().save(config);
        arenas.put(id, arena);

        return true;
    }
    public boolean setCorner2(String id, Location location) {
        Arena arena = getArena(id);

        if (arena == null)
            return false;

        arena.setCorner2(location);
        config.set("arenas." + id + ".corner2", location);
        ArenaConfig.get().save(config);
        arenas.put(id, arena);

        return true;
    }
    public boolean setEnabled(String id, Boolean enabled) {
        Arena arena = getArena(id);

        if (arena == null)
            return false;

        arena.setEnabled(enabled);;
        config.set("arenas." + id + ".enabled", enabled);
        ArenaConfig.get().save(config);
        arenas.put(id, arena);

        return true;
    }

    public Set<String> getAllArenaNames() {
        Set<String> names = ConcurrentHashMap.newKeySet();
        arenas.forEach((k, v) -> {
            names.add(k);
        });

        return names;
    }
}
