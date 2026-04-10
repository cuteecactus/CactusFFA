package me.cactusffa.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(ColorUtil.component(name).decoration(TextDecoration.ITALIC, false));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        ItemMeta meta = itemStack.getItemMeta();
        List<Component> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(ColorUtil.component(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder hideFlags() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}
