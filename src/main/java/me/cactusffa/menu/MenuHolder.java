package me.cactusffa.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class MenuHolder implements InventoryHolder {

    private final String key;
    private final MenuContext context;

    public MenuHolder(String key, MenuContext context) {
        this.key = key;
        this.context = context;
    }

    public String key() {
        return key;
    }

    public MenuContext context() {
        return context;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
