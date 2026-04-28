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
import org.bukkit.inventory.InventoryHolder;
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
    private final Map<UUID, Map<Integer, String>> adminKitSlots = new HashMap<>();

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
        Inventory inventory = createInventory(section.getInt("size", 27), section.getString("title", "&0FFA"), MenuKeys.ROOT, MenuContext.root());
        fill(section, inventory);
        for (KitCategory category : plugin.kits().categories()) {
            List<String> lore = new java.util.ArrayList<>(category.lore());
            lore.add("");
            lore.add("&8- &7Browse this category");
            lore.add("&8- &aClick to open");
            setIfInside(inventory, category.slot(), new ItemBuilder(category.icon()).name(category.displayName()).lore(lore).hideFlags().build());
        }
        for (KitDefinition kit : plugin.kits().uncategorizedKits()) {
            setIfInside(inventory, kit.slot(), kitItem(kit));
        }
        setIfInside(inventory, section.getInt("close-slot", 26), closeItem());
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void openCategory(Player player, KitCategory category) {
        ConfigurationSection section = config.getConfigurationSection("menus.category");
        if (section == null) {
            return;
        }
        String title = section.getString("title", "&0%category%").replace("%category%", category.displayName());
        Inventory inventory = createInventory(section.getInt("size", 27), title, MenuKeys.CATEGORY, MenuContext.category(category.id()));
        fill(section, inventory);
        for (KitDefinition kit : plugin.kits().kitsInCategory(category.id())) {
            setIfInside(inventory, kit.slot(), kitItem(kit));
        }
        setIfInside(inventory, section.getInt("back-slot", 18), backItem());
        setIfInside(inventory, section.getInt("close-slot", 26), closeItem());
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void openAdmin(Player player) {
        ConfigurationSection section = config.getConfigurationSection("menus.admin");
        if (section == null) {
            return;
        }
        Inventory inventory = createInventory(section.getInt("size", 27), section.getString("title", "&0CactusFFA Admin"), MenuKeys.ADMIN, MenuContext.admin());
        fill(section, inventory);
        setIfInside(inventory, section.getInt("reload-slot", 10), new ItemBuilder(Material.BOOK).name("&aReload Plugin").lore(List.of("&7Refresh configs, kits, menus,", "&7arenas and scoreboards.")).hideFlags().build());
        setIfInside(inventory, section.getInt("kit-options-slot", 11), new ItemBuilder(Material.NETHER_STAR).name("&bKit Control Center").lore(List.of("&7Open a dedicated kit browser", "&7for toggles and combat tuning.")).hideFlags().build());
        setIfInside(inventory, section.getInt("create-arena-slot", 12), new ItemBuilder(Material.LIME_CONCRETE).name("&aCreate Arena").lore(List.of("&7Command: &f/cacffa arena create <id>", "&7Creates an arena in your", "&7current world and position.")).hideFlags().build());
        setIfInside(inventory, section.getInt("set-arena-slot", 13), new ItemBuilder(Material.RECOVERY_COMPASS).name("&eUpdate Arena Spawn").lore(List.of("&7Command: &f/cacffa arena setspawn <id>", "&7Overwrite an arena location safely.")).hideFlags().build());
        setIfInside(inventory, section.getInt("teleport-arena-slot", 14), new ItemBuilder(Material.ENDER_PEARL).name("&3Teleport To Arena").lore(List.of("&7Command: &f/cacffa arena tp <id>", "&7Quick preview for testing kits.")).hideFlags().build());
        setIfInside(inventory, section.getInt("set-lobby-slot", 15), new ItemBuilder(Material.OAK_DOOR).name("&dSet Main Lobby").lore(List.of("&7Command: &f/cacffa setlobby", "&7Save your current location", "&7as the FFA exit point.")).hideFlags().build());
        setIfInside(inventory, section.getInt("world-info-slot", 16), new ItemBuilder(Material.GRASS_BLOCK).name("&fArena Placement").lore(List.of("&7Arenas can be created", "&7in any world.", "&7No dedicated FFA world is required.")).hideFlags().build());
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void openAdminKits(Player player) {
        ConfigurationSection section = config.getConfigurationSection("menus.admin-kits");
        if (section == null) {
            return;
        }
        Inventory inventory = createInventory(section.getInt("size", 54), section.getString("title", "&0Select Kit"), MenuKeys.ADMIN_KITS, MenuContext.adminKits());
        fill(section, inventory);
        Map<Integer, String> slots = new HashMap<>();
        int index = 0;
        for (KitDefinition kit : plugin.kits().kits()) {
            while (index == section.getInt("back-slot", 45) || index == section.getInt("close-slot", 53)) {
                index++;
            }
            if (index >= inventory.getSize()) {
                break;
            }
            setIfInside(inventory, index, adminKitItem(kit));
            slots.put(index, kit.id());
            index++;
        }
        adminKitSlots.put(player.getUniqueId(), slots);
        setIfInside(inventory, section.getInt("back-slot", 45), backItem());
        setIfInside(inventory, section.getInt("close-slot", 53), closeItem());
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void openAdminKitOptions(Player player, KitDefinition kit) {
        ConfigurationSection section = config.getConfigurationSection("menus.admin-kit-options");
        if (section == null) {
            return;
        }
        String title = section.getString("title", "&0%kit% Options").replace("%kit%", kit.displayName());
        Inventory inventory = createInventory(section.getInt("size", 27), title, MenuKeys.ADMIN_KIT_OPTIONS, MenuContext.adminKitOptions(kit.id()));
        fill(section, inventory);
        setIfInside(inventory, section.getInt("regen-slot", 10), toggleItem(Material.GOLDEN_APPLE, "&aRegen After Kill", kit.options().regenAfterKill()));
        setIfInside(inventory, section.getInt("rekit-slot", 11), toggleItem(Material.CHEST, "&bRekit After Kill", kit.options().rekitAfterKill()));
        setIfInside(inventory, section.getInt("combat-minus-slot", 12), new ItemBuilder(Material.RED_WOOL).name("&cCombat -1s").lore(List.of("&7Decrease this kit's", "&7combat timer.")).build());
        setIfInside(inventory, section.getInt("combat-info-slot", 13), new ItemBuilder(Material.CLOCK).name("&eCombat Timer").lore(List.of("&7Current: &f" + kit.options().combatLogSeconds() + "s")).build());
        setIfInside(inventory, section.getInt("combat-plus-slot", 14), new ItemBuilder(Material.LIME_WOOL).name("&aCombat +1s").lore(List.of("&7Increase this kit's", "&7combat timer.")).build());
        setIfInside(inventory, section.getInt("health-slot", 15), toggleItem(Material.NAME_TAG, "&dShow Health Below Name", kit.options().showHealthBelowName()));
setIfInside(inventory, section.getInt("drops-slot", 16), toggleItem(Material.DROPPER, "&6Drop Items On Kill", kit.options().dropItemsOnKill()));
        setIfInside(inventory, section.getInt("hunger-slot", 19), toggleItem(Material.COOKED_BEEF, "&eHunger", kit.options().hunger()));
        setIfInside(inventory, section.getInt("saturation-slot", 20), toggleItem(Material.GOLDEN_CARROT, "&eSaturation", kit.options().saturation()));
        setIfInside(inventory, section.getInt("pickup-slot", 21), toggleItem(Material.HOPPER, "&ePickup Items", kit.options().pickupItems()));
        setIfInside(inventory, section.getInt("back-slot", 18), backItem());
        setIfInside(inventory, section.getInt("close-slot", 26), closeItem());
        player.openInventory(inventory);
        play(player, "menu-open");
    }

    public void handleClick(Player player, Inventory inventory, int slot) {
        MenuContext context = context(inventory);
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
                        if (plugin.combat().isTagged(player.getUniqueId())) {
                            plugin.messages().send(player, "combat-blocked");
                            return;
                        }
                        plugin.arenas().find(kit.arenaId()).ifPresent(arena -> {
                            plugin.sessions().join(player, kit, arena);
                            plugin.messages().send(player, "joined-kit", Map.of("kit", kit.displayName(), "arena", arena.displayName()));
                        });
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
                        if (plugin.combat().isTagged(player.getUniqueId())) {
                            plugin.messages().send(player, "combat-blocked");
                            return;
                        }
                        plugin.arenas().find(kit.arenaId()).ifPresent(arena -> {
                            plugin.sessions().join(player, kit, arena);
                            plugin.messages().send(player, "joined-kit", Map.of("kit", kit.displayName(), "arena", arena.displayName()));
                        });
                        return;
                    }
                }
            }
            case ADMIN -> {
                if (slot == getSlot("menus.admin.reload-slot", 10)) {
                    plugin.reloadPlugin();
                    plugin.messages().send(player, "config-reloaded");
                    play(player, "join-kit");
                    return;
                }
                if (slot == getSlot("menus.admin.kit-options-slot", 11)) {
                    openAdminKits(player);
                    return;
                }
                if (slot == getSlot("menus.admin.set-lobby-slot", 15)) {
                    plugin.worlds().setMainLobby(player.getLocation());
                    plugin.messages().send(player, "lobby-set");
                    return;
                }
            }
            case ADMIN_KITS -> {
                if (slot == getSlot("menus.admin-kits.back-slot", 45)) {
                    openAdmin(player);
                    return;
                }
                if (slot == getSlot("menus.admin-kits.close-slot", 53)) {
                    player.closeInventory();
                    return;
                }
                Map<Integer, String> slots = adminKitSlots.get(player.getUniqueId());
                if (slots != null && slots.containsKey(slot)) {
                    plugin.kits().kit(slots.get(slot)).ifPresent(kit -> openAdminKitOptions(player, kit));
                    return;
                }
            }
            case ADMIN_KIT_OPTIONS -> {
                KitDefinition kit = plugin.kits().kit(context.id()).orElse(null);
                if (kit == null) {
                    openAdminKits(player);
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.back-slot", 18)) {
                    openAdminKits(player);
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.close-slot", 26)) {
                    player.closeInventory();
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.regen-slot", 10)) {
                    plugin.kits().toggleOption(kit.id(), "regen-after-kill");
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.rekit-slot", 11)) {
                    plugin.kits().toggleOption(kit.id(), "rekit-after-kill");
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.combat-minus-slot", 12)) {
                    plugin.kits().setCombatLogSeconds(kit.id(), kit.options().combatLogSeconds() - 1);
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.combat-plus-slot", 14)) {
                    plugin.kits().setCombatLogSeconds(kit.id(), kit.options().combatLogSeconds() + 1);
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.health-slot", 15)) {
                    plugin.kits().toggleOption(kit.id(), "show-health-below-name");
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
if (slot == getSlot("menus.admin-kit-options.drops-slot", 16)) {
                    plugin.kits().toggleOption(kit.id(), "drop-items-on-kill");
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.hunger-slot", 19)) {
                    plugin.kits().toggleOption(kit.id(), "hunger");
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.saturation-slot", 20)) {
                    plugin.kits().toggleOption(kit.id(), "saturation");
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                    return;
                }
                if (slot == getSlot("menus.admin-kit-options.pickup-slot", 21)) {
                    plugin.kits().toggleOption(kit.id(), "pickup-items");
                    openAdminKitOptions(player, plugin.kits().kit(kit.id()).orElse(kit));
                }
            }
        }
    }

    public boolean isManaged(Player player) {
        return managed(player.getOpenInventory().getTopInventory());
    }

    public boolean isManaged(Inventory inventory) {
        return managed(inventory);
    }

    public void clear(Player player) {
        adminKitSlots.remove(player.getUniqueId());
    }

    private ItemStack kitItem(KitDefinition kit) {
        List<String> lore = new java.util.ArrayList<>(kit.lore());
        lore.add("");
        lore.add("&8- &7Arena: &f" + kit.arenaId());
        lore.add("&8- &7Combat: &f" + kit.options().combatLogSeconds() + "s");
        lore.add("&8- &aClick to join");
        return new ItemBuilder(kit.icon()).name(kit.displayName()).lore(lore).hideFlags().build();
    }

private ItemStack adminKitItem(KitDefinition kit) {
        List<String> lore = new java.util.ArrayList<>(kit.lore());
        lore.add("");
        lore.add("&8- &7Regen Kill: " + status(kit.options().regenAfterKill()));
        lore.add("&8- &7Rekit Kill: " + status(kit.options().rekitAfterKill()));
        lore.add("&8- &7Combat: &f" + kit.options().combatLogSeconds() + "s");
        lore.add("&8- &7Below Name HP: " + status(kit.options().showHealthBelowName()));
        lore.add("&8- &7Drop Items: " + status(kit.options().dropItemsOnKill()));
        lore.add("&8- &7Hunger: " + status(kit.options().hunger()));
        lore.add("&8- &7Saturation: " + status(kit.options().saturation()));
        lore.add("&8- &bClick to manage");
        return new ItemBuilder(kit.icon()).name(kit.displayName()).lore(lore).hideFlags().build();
    }

    private ItemStack toggleItem(Material material, String name, boolean enabled) {
        return new ItemBuilder(material).name(name).lore(List.of("&7Current: " + status(enabled), "&aClick to toggle")).build();
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

    private String status(boolean enabled) {
        return enabled ? "&aEnabled" : "&cDisabled";
    }

    private Inventory createInventory(int size, String title, String key, MenuContext context) {
        return plugin.getServer().createInventory(new MenuHolder(key, context), size, ColorUtil.component(title));
    }

    private boolean managed(Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof MenuHolder;
    }

    private MenuContext context(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof MenuHolder menuHolder) {
            return menuHolder.context();
        }
        return null;
    }

    private int getSlot(String path, int fallback) {
        return config.getInt(path, fallback);
    }

    private void setIfInside(Inventory inventory, int slot, ItemStack item) {
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
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
