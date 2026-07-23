package dev.cuteecactus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import dev.cuteecactus.arena.ArenaCleanupManager;
import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak (BlockBreakEvent e) {
        Player player = e.getPlayer();

        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null) return;
        Kit kit = profile.getCurrentKit();

        if (kit == null) return;
        
        if (!kit.getBreakableBlocks().isEmpty() && !kit.getBreakableBlocks().contains(e.getBlock().getType())) {
            e.setCancelled(true);
            return;
        }
        
        if (kit.getRule("break-blocks") == false) {
            e.setCancelled(true);
            return;
        }
        ArenaCleanupManager.get().trackBlockChange(e.getBlock().getLocation(), e.getBlock().getState());

    }
    
}
