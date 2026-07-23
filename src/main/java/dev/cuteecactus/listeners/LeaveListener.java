package dev.cuteecactus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.cuteecactus.ffa.FFAManager;
import dev.cuteecactus.profile.ProfileManager;

public class LeaveListener implements Listener{

    @EventHandler
    public void onLeave (PlayerQuitEvent e) {
        FFAManager.get().removePlayerFromAll(e.getPlayer().getUniqueId());
        ProfileManager.get().removeProfile(e.getPlayer().getUniqueId());
    }
    
}
