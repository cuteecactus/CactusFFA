package dev.cuteecactus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;
import dev.cuteecactus.profile.ProfileState;

public class RegainHealthListener implements Listener {
    @EventHandler
    public void onHealthRegain (EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile.getProfileState() != ProfileState.IN_FFA) return;

        Kit kit = profile.getCurrentKit();

        if (kit == null) return;

        boolean saturationRule = kit.getRule("saturation");
        
        if (e.getRegainReason() == RegainReason.SATIATED && saturationRule == true) {
            e.setCancelled(true);
        }
    }
}
