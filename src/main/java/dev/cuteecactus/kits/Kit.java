package dev.cuteecactus.kits;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit {
    private final String id;
    private final ConcurrentHashMap<String, Boolean> kitRules = new ConcurrentHashMap<>();
    
    private String displayName;
    private ItemStack[] content;
    private boolean enabled = true;
    private Material icon = Material.IRON_SWORD;
    private Set <Material> breakableBlocks = ConcurrentHashMap.newKeySet();
    
    public Kit(String id) {
        this.id = id;
    }

    public void applyContent (Player player) {
        player.getInventory().clear();
        player.getInventory().setContents(content);
    }

    public void setContent(ItemStack[] content) {
        this.content = content;
    }

    public ItemStack[] getContent() {
        return content;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public String getId() {
        return id;
    }

    public ConcurrentHashMap<String, Boolean> getKitRules() {
        return kitRules;
    }

    public void addKitRule (String rule, boolean enable) {
        kitRules.put(rule, enable);
    }

    public boolean getRule (String rule) {
        return kitRules.getOrDefault(rule, false);
    }

    public Set<Material> getBreakableBlocks() {
        return breakableBlocks;
    }
    
    public void setBreakableBlocks(Set<Material> breakableBlocks) {
        this.breakableBlocks = breakableBlocks;
    }

    public void addBreakableBlock (Material material) {
        this.breakableBlocks.add(material);
    }
    public void removeBreakableBlock (Material material) {
        this.breakableBlocks.remove(material);
    }

}
