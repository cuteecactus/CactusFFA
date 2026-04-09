package me.cactusffa.model;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public record PlayerSnapshot(
        Location location,
        ItemStack[] contents,
        ItemStack[] armor,
        ItemStack[] extra,
        double health,
        int foodLevel,
        float saturation,
        int level,
        float exp,
        GameMode gameMode,
        Collection<PotionEffect> potionEffects
) {
}
