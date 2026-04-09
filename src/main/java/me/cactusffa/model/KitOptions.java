package me.cactusffa.model;

public record KitOptions(
        boolean regenAfterKill,
        boolean rekitAfterKill,
        int combatLogSeconds,
        boolean showHealthBelowName,
        boolean dropItemsOnKill
) {
}
