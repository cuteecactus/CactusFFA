package dev.cuteecactus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.cuteecactus.profile.ProfileManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin (PlayerJoinEvent e) {
        ProfileManager.get().addProfile(e.getPlayer().getUniqueId());
    }
    
}
