package dev.cuteecactus.model;

public record KitOptions(
        boolean regenAfterKill,
        boolean rekitAfterKill,
        int combatLogSeconds,
        boolean showHealthBelowName,
        boolean dropItemsOnKill,
        boolean hunger,
        boolean saturation,
        boolean pickupItems
) {
}
