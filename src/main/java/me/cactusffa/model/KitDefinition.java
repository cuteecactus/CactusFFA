package me.cactusffa.model;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public record KitDefinition(
        String id,
        String displayName,
        String categoryId,
        Material icon,
        int slot,
        String arenaId,
        String permission,
        List<String> lore,
        ItemStack[] contents,
        ItemStack[] armor,
        ItemStack[] extras
) {
}
