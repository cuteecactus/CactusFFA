package me.cactusffa.service;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.KitCategory;
import me.cactusffa.model.KitDefinition;
import me.cactusffa.util.ItemSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class KitManager {

    private final CactusFFAPlugin plugin;
    private final File file;
    private final Map<String, KitCategory> categories = new LinkedHashMap<>();
    private final Map<String, KitDefinition> kits = new LinkedHashMap<>();

    public KitManager(CactusFFAPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "kits.yml");
    }

    public void reload() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        categories.clear();
        kits.clear();

        ConfigurationSection categorySection = config.getConfigurationSection("categories");
        if (categorySection != null) {
            for (String id : categorySection.getKeys(false)) {
                ConfigurationSection section = categorySection.getConfigurationSection(id);
                if (section == null) {
                    continue;
                }
                categories.put(id.toLowerCase(Locale.ROOT), new KitCategory(
                        id.toLowerCase(Locale.ROOT),
                        section.getString("display-name", id),
                        material(section.getString("icon", "CHEST")),
                        section.getInt("slot", 0),
                        section.getStringList("lore")
                ));
            }
        }

        ConfigurationSection kitSection = config.getConfigurationSection("kits");
        if (kitSection != null) {
            for (String id : kitSection.getKeys(false)) {
                ConfigurationSection section = kitSection.getConfigurationSection(id);
                if (section == null) {
                    continue;
                }
                ItemStack[] inventory = ItemSerializer.decodeItems(section.getString("inventory-base64", ""));
                ItemStack[] armor = ItemSerializer.decodeItems(section.getString("armor-base64", ""));
                ItemStack[] extra = ItemSerializer.decodeItems(section.getString("extra-base64", ""));
                kits.put(id.toLowerCase(Locale.ROOT), new KitDefinition(
                        id.toLowerCase(Locale.ROOT),
                        section.getString("display-name", id),
                        section.getString("category", "").toLowerCase(Locale.ROOT),
                        material(section.getString("icon", "CHEST")),
                        section.getInt("slot", 0),
                        section.getString("arena", "").toLowerCase(Locale.ROOT),
                        section.getString("permission", ""),
                        section.getStringList("lore"),
                        inventory,
                        armor,
                        extra
                ));
            }
        }
    }

    public Collection<KitCategory> categories() {
        return categories.values().stream()
                .sorted(Comparator.comparingInt(KitCategory::slot))
                .collect(Collectors.toList());
    }

    public Optional<KitCategory> category(String id) {
        return Optional.ofNullable(categories.get(id.toLowerCase(Locale.ROOT)));
    }

    public Optional<KitDefinition> kit(String id) {
        return Optional.ofNullable(kits.get(id.toLowerCase(Locale.ROOT)));
    }

    public Optional<KitDefinition> resolve(String[] args) {
        if (args.length == 0) {
            return Optional.empty();
        }
        if (args.length == 1) {
            return kit(args[0]);
        }
        return kit(String.join("_", args).toLowerCase(Locale.ROOT));
    }

    public List<KitDefinition> kitsInCategory(String categoryId) {
        List<KitDefinition> list = new ArrayList<>();
        for (KitDefinition definition : kits.values()) {
            if (definition.categoryId().equalsIgnoreCase(categoryId)) {
                list.add(definition);
            }
        }
        list.sort(Comparator.comparingInt(KitDefinition::slot));
        return list;
    }

    public Collection<KitDefinition> kits() {
        return Collections.unmodifiableCollection(kits.values());
    }

    public List<KitDefinition> uncategorizedKits() {
        List<KitDefinition> list = new ArrayList<>();
        for (KitDefinition definition : kits.values()) {
            if (definition.categoryId().isBlank()) {
                list.add(definition);
            }
        }
        list.sort(Comparator.comparingInt(KitDefinition::slot));
        return list;
    }

    public boolean createCategory(String id) {
        String normalizedId = normalizeId(id);
        if (normalizedId.isBlank() || categories.containsKey(normalizedId)) {
            return false;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = "categories." + normalizedId;
        config.set(path + ".display-name", prettify(normalizedId));
        config.set(path + ".icon", Material.CHEST.name());
        config.set(path + ".slot", nextCategorySlot());
        config.set(path + ".lore", List.of("&7Custom kit category."));
        return save(config);
    }

    public boolean createKit(String id, String arenaId, String categoryId, Material icon) {
        String normalizedId = normalizeId(id);
        String normalizedArenaId = normalizeId(arenaId);
        String normalizedCategoryId = normalizeOptionalId(categoryId);
        if (normalizedId.isBlank() || normalizedArenaId.isBlank() || kits.containsKey(normalizedId)) {
            return false;
        }
        if (!normalizedCategoryId.isBlank() && !categories.containsKey(normalizedCategoryId)) {
            return false;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = "kits." + normalizedId;
        config.set(path + ".display-name", prettify(normalizedId));
        config.set(path + ".category", normalizedCategoryId);
        config.set(path + ".icon", (icon == null ? Material.CHEST : icon).name());
        config.set(path + ".slot", nextKitSlot(normalizedCategoryId));
        config.set(path + ".arena", normalizedArenaId);
        config.set(path + ".permission", "");
        config.set(path + ".lore", List.of("&7Custom FFA kit."));
        config.set(path + ".inventory-base64", "");
        config.set(path + ".armor-base64", "");
        config.set(path + ".extra-base64", "");
        return save(config);
    }

    public boolean setKitInventory(String id, ItemStack[] contents, ItemStack[] armor, ItemStack[] extra) {
        String normalizedId = normalizeId(id);
        if (!kits.containsKey(normalizedId)) {
            return false;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = "kits." + normalizedId;
        if (!config.contains(path)) {
            return false;
        }
        config.set(path + ".inventory-base64", ItemSerializer.encodeItems(contents));
        config.set(path + ".armor-base64", ItemSerializer.encodeItems(armor));
        config.set(path + ".extra-base64", ItemSerializer.encodeItems(extra));
        return save(config);
    }

    public String normalizeId(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    public String normalizeOptionalId(String raw) {
        String normalized = normalizeId(raw);
        if (normalized.equals("none") || normalized.equals("null") || normalized.equals("-")) {
            return "";
        }
        return normalized;
    }

    public String prettify(String id) {
        if (id == null || id.isBlank()) {
            return "";
        }
        String[] parts = id.replace('-', '_').split("_");
        List<String> words = new ArrayList<>();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            words.add(part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1));
        }
        return String.join(" ", words);
    }

    private int nextCategorySlot() {
        return categories.values().stream()
                .mapToInt(KitCategory::slot)
                .max()
                .orElse(9) + 1;
    }

    private int nextKitSlot(String categoryId) {
        return kits.values().stream()
                .filter(kit -> kit.categoryId().equalsIgnoreCase(categoryId))
                .mapToInt(KitDefinition::slot)
                .max()
                .orElse(9) + 1;
    }

    private boolean save(YamlConfiguration config) {
        try {
            config.save(file);
            reload();
            return true;
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save kits.yml: " + exception.getMessage());
            return false;
        }
    }

    private Material material(String raw) {
        try {
            return Material.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return Material.CHEST;
        }
    }
}
