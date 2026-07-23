package dev.cuteecactus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;

public class BlockPlaceListener implements Listener{
    @EventHandler
    public void onBlockPlace (BlockPlaceEvent e) {
        Player player = e.getPlayer();

        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null) return;
        Kit kit = profile.getCurrentKit();
        if (kit == null) return;

        if (kit.getRule("place-blocks") == false) {
            e.setCancelled(true); 
        }
    }
}
