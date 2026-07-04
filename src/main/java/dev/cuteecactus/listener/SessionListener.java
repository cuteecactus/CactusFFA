package dev.cuteecactus.listener;

import dev.cuteecactus.CactusFFAPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class SessionListener implements Listener {

    private final CactusFFAPlugin plugin;

    public SessionListener(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.sessions().get(player).ifPresent(session -> plugin.kits().kit(session.currentKitId()).ifPresent(kit ->
                plugin.arenas().find(kit.arenaId()).ifPresent(arena -> {
                    event.setRespawnLocation(arena.spawn());
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.sessions().join(player, kit, arena));
                })));
    }
}
