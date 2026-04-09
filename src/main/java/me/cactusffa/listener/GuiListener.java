package me.cactusffa.listener;

import me.cactusffa.CactusFFAPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

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
        if (!plugin.menus().isManaged(player)) {
            return;
        }
        event.setCancelled(true);
        plugin.menus().handleClick(player, event.getRawSlot());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            plugin.menus().clear(player);
        }
    }
}
