package me.cactusffa.service;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.util.LocationUtil;
import me.cactusffa.world.VoidChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public final class WorldService {

    private final CactusFFAPlugin plugin;

    public WorldService(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    public void prepareArenaWorld() {
        String worldName = plugin.getConfig().getString("world.arena-world", "ffa_arenas");
        boolean create = plugin.getConfig().getBoolean("world.create-void-world", true);
        World world = Bukkit.getWorld(worldName);
        if (world == null && create) {
            WorldCreator creator = new WorldCreator(worldName);
            creator.generator(new VoidChunkGenerator());
            world = creator.createWorld();
        }
        if (world != null) {
            world.setAutoSave(false);
            world.setKeepSpawnInMemory(plugin.getConfig().getBoolean("world.force-load-spawn-chunk", true));
            plugin.messages().send(Bukkit.getConsoleSender(), "world-created", Map.of("world", world.getName()));
        }
    }

    public String arenaWorldName() {
        return plugin.getConfig().getString("world.arena-world", "ffa_arenas");
    }

    public boolean isArenaWorld(World world) {
        return world != null && world.getName().equalsIgnoreCase(arenaWorldName());
    }

    public Location fallbackLeaveLocation() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ffa.leave.fallback-location");
        return LocationUtil.fromSection(section);
    }
}
