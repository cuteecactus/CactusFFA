package me.cactusffa.listener;

import me.cactusffa.CactusFFAPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public final class ProtectionListener implements Listener {

    private final CactusFFAPlugin plugin;

    public ProtectionListener(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (plugin.sessions().isInFfa(event.getPlayer()) && plugin.getConfig().getBoolean("ffa.protection.block-break", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.sessions().isInFfa(event.getPlayer()) && plugin.getConfig().getBoolean("ffa.protection.block-place", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.sessions().isInFfa(event.getPlayer())) {
            return;
        }
        boolean allowPlayerDrop = plugin.getConfig().getBoolean("ffa.player-inventory.allow-item-drop", true);
        boolean allowProtectedDrop = plugin.getConfig().getBoolean("ffa.protection.item-drop", false);
        if (!allowPlayerDrop || !allowProtectedDrop) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (plugin.sessions().isInFfa(event.getPlayer()) && !plugin.getConfig().getBoolean("ffa.protection.item-pickup", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && plugin.sessions().isInFfa(player) && !plugin.getConfig().getBoolean("ffa.protection.hunger", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!plugin.sessions().isInFfa(player)) {
            return;
        }
        if (plugin.menus().isManaged(event.getView().getTopInventory())) {
            return;
        }
        if (plugin.getConfig().getBoolean("ffa.player-inventory.allow-item-rearrange", true)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!plugin.sessions().isInFfa(player)) {
            return;
        }
        if (plugin.menus().isManaged(event.getView().getTopInventory())) {
            return;
        }
        if (plugin.getConfig().getBoolean("ffa.player-inventory.allow-item-rearrange", true)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (plugin.worlds().isArenaWorld(event.getWorld()) && plugin.getConfig().getBoolean("ffa.protection.weather-change", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (plugin.worlds().isArenaWorld(event.getLocation().getWorld())
                && event.getEntityType() != EntityType.PLAYER
                && plugin.getConfig().getBoolean("ffa.protection.mob-spawn", true)) {
            event.setCancelled(true);
        }
    }
}
