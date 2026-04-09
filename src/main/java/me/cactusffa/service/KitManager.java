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
    private final Map<String, KitCategory> categories = new LinkedHashMap<>();
    private final Map<String, KitDefinition> kits = new LinkedHashMap<>();

    public KitManager(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "kits.yml"));
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

    private Material material(String raw) {
        try {
            return Material.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return Material.CHEST;
        }
    }
}
