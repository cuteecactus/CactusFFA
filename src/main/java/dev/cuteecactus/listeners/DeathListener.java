package dev.cuteecactus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import dev.cuteecactus.arena.Arena;
import dev.cuteecactus.ffa.FFAManager;
import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;

public class DeathListener implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!(e.getEntity() instanceof Player player))
            return;
        e.setCancelled(true);
        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null)
            return;
        Kit kit = profile.getCurrentKit();
        if (kit == null)
            return;

        if (kit.getRule("drop-items") == false) {
            e.getDrops().clear();
        }

        if (kit.getRule("death-lobby") == true) {
            FFAManager.get().leaveFFA(player);
            return;
        }

        // e.setShouldPlayDeathAnimation(false);
        // e.setDeathDuration(0);

        Arena arena = kit.getArena();
        // Bukkit.getScheduler().runTask(CactusFFA.get(), () -> {
        // player.respawn();
        if (arena != null && arena.getSpawn() != null) {
            player.teleport(arena.getSpawn());
        }
        player.getInventory().setContents(kit.getContent());
        // });
    }

}
