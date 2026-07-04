package dev.cuteecactus.listener;

import dev.cuteecactus.CactusFFAPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class GuiListener implements Listener {

    private final CactusFFAPlugin plugin;

    public GuiListener(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!plugin.menus().isManaged(event.getView().getTopInventory())) {
            return;
        }
        event.setCancelled(true);
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        plugin.menus().handleClick(player, event.getView().getTopInventory(), event.getRawSlot());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!plugin.menus().isManaged(event.getView().getTopInventory())) {
            return;
        }
        int topSize = event.getView().getTopInventory().getSize();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!plugin.menus().isManaged(player.getOpenInventory().getTopInventory())) {
                    plugin.menus().clear(player);
                }
            });
        }
    }
}
