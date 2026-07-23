package dev.cuteecactus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;

public class FoodLevelChangeListener implements Listener {
    @EventHandler
    public void onFoodLevelChange (FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null) return;

        Kit kit = profile.getCurrentKit();
        if (kit == null) return;

        if (kit.getRule("hunger") == true) {
            e.setCancelled(true);
        }
    }
}
