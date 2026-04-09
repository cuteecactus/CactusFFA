package me.cactusffa.model;

import java.util.List;
import org.bukkit.Material;

public record KitCategory(String id, String displayName, Material icon, int slot, List<String> lore) {
}
