package dev.cuteecactus.arena;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;

import dev.cuteecactus.CactusFFA;
import dev.cuteecactus.config.BaseConfig;

public class ArenaCleanupManager {
    private static ArenaCleanupManager instance;

    private final Map<Location, BlockState> changedBlocks = new ConcurrentHashMap<>();
    private int taskId = -1;

    public ArenaCleanupManager() {
        instance = this;
        startScheduler();
    }

    public static ArenaCleanupManager get() {
        return instance;
    }

    public void trackBlockChange(Location loc, BlockState originalState) {
        changedBlocks.putIfAbsent(loc, originalState);
    }

    public void cleanup() {
        for (Map.Entry<Location, BlockState> entry : changedBlocks.entrySet()) {
            Location loc = entry.getKey();
            BlockState original = entry.getValue();
            loc.getBlock().setType(original.getType());
            loc.getBlock().setBlockData(original.getBlockData());
        }
        changedBlocks.clear();
    }

    private void startScheduler() {
        int minutes = BaseConfig.get().getConfig().getInt("cleanup-duration", 30);
        if (minutes <= 0) return;

        long intervalTicks = minutes * 60L * 20L;
        taskId = Bukkit.getScheduler().runTaskTimer(CactusFFA.get(), this::cleanup, intervalTicks, intervalTicks).getTaskId();
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
