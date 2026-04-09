package me.cactusffa.service;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.util.LocationUtil;
import me.cactusffa.world.VoidChunkGenerator;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

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
            creator.environment(World.Environment.NORMAL);
            creator.generateStructures(false);
            creator.generator(new VoidChunkGenerator());
            world = creator.createWorld();
        }
        if (world != null) {
            world.setAutoSave(false);
            world.setKeepSpawnInMemory(plugin.getConfig().getBoolean("world.force-load-spawn-chunk", true));
            world.setSpawnLocation(0, 64, 0);
            ensureSpawnPlatform(world);
            tryImportToMultiverse(world);
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

    private void ensureSpawnPlatform(World world) {
        Location center = new Location(world, 0.0D, 64.0D, 0.0D);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                world.getBlockAt(x, 63, z).setType(Material.BEDROCK, false);
            }
        }
        world.getBlockAt(0, 64, 0).setType(Material.BARRIER, false);
        world.getBlockAt(0, 64, 0).setType(Material.AIR, false);
        world.setSpawnLocation(center);
    }

    private void tryImportToMultiverse(World world) {
        Plugin multiverse = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        if (multiverse == null || !multiverse.isEnabled()) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " + world.getName() + " normal");
            } catch (Exception exception) {
                plugin.getLogger().warning("Failed to auto-import arena world into Multiverse: " + exception.getMessage());
            }
        });
    }
}
