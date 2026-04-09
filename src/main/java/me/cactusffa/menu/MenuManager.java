package me.cactusffa.menu;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.KitCategory;
import me.cactusffa.model.KitDefinition;
import me.cactusffa.util.ColorUtil;
import me.cactusffa.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class MenuManager {

    private final CactusFFAPlugin plugin;
    private YamlConfiguration config;
    private final Map<UUID, MenuContext> contexts = new HashMap<>();

    public MenuManager(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "menus.yml"));
    }

    public void openRoot(Player player) {
        ConfigurationSection section = config.getConfigurationSection("menus.root");
        if (section == null) {
            return;
        }
        Inventory inventory = plugin.getServer().createInventory(player, section.getInt("size", 27), ColorUtil.component(section.getString("title", "&0FFA")));
        fill(section, inventory);
        for (KitCategory category : plugin.kits().categories()) {
            inventory.setItem(category.slot(), new ItemBuilder(category.icon()).name(category.displayName()).lore(category.lore()).hideFlags().build());
        }
        for (KitDefinition kit : plugin.kits().uncategorizedKits()) {
            inventory.setItem(kit.slot(), kitItem(kit));
        }
        inventory.setItem(section.getInt("close-slot", 26), closeItem());
        contexts.put(player.getUniqueId(), MenuContext.root());
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void openCategory(Player player, KitCategory category) {
        ConfigurationSection section = config.getConfigurationSection("menus.category");
        if (section == null) {
            return;
        }
        String title = section.getString("title", "&0%category%").replace("%category%", category.displayName());
        Inventory inventory = plugin.getServer().createInventory(player, section.getInt("size", 27), ColorUtil.component(title));
        fill(section, inventory);
        for (KitDefinition kit : plugin.kits().kitsInCategory(category.id())) {
            inventory.setItem(kit.slot(), kitItem(kit));
        }
        inventory.setItem(section.getInt("back-slot", 18), backItem());
        inventory.setItem(section.getInt("close-slot", 26), closeItem());
        contexts.put(player.getUniqueId(), MenuContext.category(category.id()));
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void openAdmin(Player player) {
        ConfigurationSection section = config.getConfigurationSection("menus.admin");
        if (section == null) {
            return;
        }
        Inventory inventory = plugin.getServer().createInventory(player, section.getInt("size", 27), ColorUtil.component(section.getString("title", "&0CactusFFA Admin")));
        fill(section, inventory);
        inventory.setItem(section.getInt("reload-slot", 10), new ItemBuilder(Material.BOOK).name("&aReload").lore(List.of("&7Reload all configs and menus.")).build());
        inventory.setItem(section.getInt("create-arena-slot", 12), new ItemBuilder(Material.LIME_WOOL).name("&aCreate Arena").lore(List.of("&7Use /cactusffa arena create <id>", "&7while standing in the arena world.")).build());
        inventory.setItem(section.getInt("set-arena-slot", 13), new ItemBuilder(Material.COMPASS).name("&eSet Arena Spawn").lore(List.of("&7Use /cactusffa arena setspawn <id>", "&7to update the selected arena.")).build());
        inventory.setItem(section.getInt("teleport-arena-slot", 14), new ItemBuilder(Material.ENDER_PEARL).name("&bTeleport Arena").lore(List.of("&7Use /cactusffa arena tp <id>", "&7to preview an arena.")).build());
        inventory.setItem(section.getInt("world-info-slot", 16), new ItemBuilder(Material.GRASS_BLOCK).name("&fArena World").lore(List.of("&7World: &a" + plugin.worlds().arenaWorldName(), "&7Void generation is configurable.")).build());
        contexts.put(player.getUniqueId(), MenuContext.admin());
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void handleClick(Player player, int slot) {
        MenuContext context = contexts.get(player.getUniqueId());
        if (context == null) {
            return;
        }
        switch (context.type()) {
            case ROOT -> {
                if (slot == getSlot("menus.root.close-slot", 26)) {
                    player.closeInventory();
                    return;
                }
                for (KitCategory category : plugin.kits().categories()) {
                    if (category.slot() == slot) {
                        openCategory(player, category);
                        return;
                    }
                }
                for (KitDefinition kit : plugin.kits().uncategorizedKits()) {
                    if (kit.slot() == slot) {
                        plugin.getServer().dispatchCommand(player, "ffa " + kit.id());
                        return;
                    }
                }
            }
            case CATEGORY -> {
                if (slot == getSlot("menus.category.back-slot", 18)) {
                    openRoot(player);
                    return;
                }
                if (slot == getSlot("menus.category.close-slot", 26)) {
                    player.closeInventory();
                    return;
                }
                for (KitDefinition kit : plugin.kits().kitsInCategory(context.id())) {
                    if (kit.slot() == slot) {
                        plugin.getServer().dispatchCommand(player, "ffa " + kit.id());
                        return;
                    }
                }
            }
            case ADMIN -> {
                if (slot == getSlot("menus.admin.reload-slot", 10)) {
                    plugin.reloadPlugin();
                    plugin.messages().send(player, "config-reloaded");
                    play(player, "join-kit");
                }
            }
        }
    }

    public boolean isManaged(Player player) {
        return contexts.containsKey(player.getUniqueId());
    }

    public void clear(Player player) {
        contexts.remove(player.getUniqueId());
    }

    private ItemStack kitItem(KitDefinition kit) {
        List<String> lore = new java.util.ArrayList<>(kit.lore());
        lore.add("");
        lore.add("&7Arena: &f" + kit.arenaId());
        lore.add("&aClick to join");
        return new ItemBuilder(kit.icon()).name(kit.displayName()).lore(lore).hideFlags().build();
    }

    private void fill(ConfigurationSection section, Inventory inventory) {
        if (!section.getBoolean("fill.enabled", true)) {
            return;
        }
        Material material;
        try {
            material = Material.valueOf(section.getString("fill.material", "BLACK_STAINED_GLASS_PANE").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            material = Material.BLACK_STAINED_GLASS_PANE;
        }
        ItemStack item = new ItemBuilder(material).name(section.getString("fill.name", " ")).build();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
    }

    private ItemStack backItem() {
        return new ItemBuilder(Material.ARROW).name("&eBack").lore(List.of("&7Return to categories.")).build();
    }

    private ItemStack closeItem() {
        return new ItemBuilder(Material.BARRIER).name("&cClose").lore(List.of("&7Close this menu.")).build();
    }

    private int getSlot(String path, int fallback) {
        return config.getInt(path, fallback);
    }

    private void play(Player player, String path) {
        String name = plugin.getConfig().getString("sounds." + path, "");
        if (name == null || name.isBlank()) {
            return;
        }
        try {
            player.playSound(player.getLocation(), Sound.valueOf(name), 1.0F, 1.0F);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
