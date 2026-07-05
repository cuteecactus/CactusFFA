package dev.cuteecactus.kits;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.cuteecactus.CactusFFA;
import dev.cuteecactus.config.KitsConfig;

public class KitManager {
    private static KitManager instance;

    private final ConcurrentHashMap<String, Kit> kits = new ConcurrentHashMap<>();

    private FileConfiguration config = KitsConfig.get().getConfig();

    public KitManager() {
        instance = this;

        init();
    }

    public static KitManager get() {
        return instance;
    }

    private void init() {
        for (String key : config.getConfigurationSection("kits").getKeys(false)) {
            String path = "kits." + key + ".";
            Kit kit = new Kit(key);

            String displayName = config.getString(path + "name");

            if (displayName == null)
                displayName = key;
            kit.setDisplayName(displayName);
            kit.setEnabled(config.getBoolean(path + "enabled"));
            kit.setIcon(Material.matchMaterial(config.getString(path + "icon")));

            Object raw = config.get(path + "content");
            ItemStack[] content = null;
            if (raw instanceof ItemStack[]) {
                content = (ItemStack[]) raw;
            } else if (raw instanceof List<?>) {
                List<?> list = (List<?>) raw;
                content = list.toArray(new ItemStack[0]);
            }

            if (content == null) {
                CactusFFA.get().getLogger()
                        .warning("Kit " + key + " has invalid or empty items, skipping.");
                return;
            }

            kit.setContent(content);

            kits.put(key.toLowerCase(), kit);
        }
    }

    public boolean createKit(String id, Player player) {
        if (id == null || player == null || kits.contains(id.toLowerCase()))
            return false;

        Kit kit = new Kit(id);
        kit.setDisplayName(id);
        kit.setContent(player.getInventory().getContents());
        kit.setEnabled(true);

        String path = "kits." + id + ".";

        config.set(path + "name", kit.getDisplayName());
        config.set(path + "enabled", kit.getEnabled());
        config.set(path + "icon", kit.getIcon().name());
        config.set(path + "content", kit.getContent());

        KitsConfig.get().save(config);
        kits.put(kit.getId().toLowerCase(), kit);
        return true;
    }

    public Set<String> getAllNames() {
        Set<String> names = ConcurrentHashMap.newKeySet();
        kits.forEach((k, v) -> {
            names.add(k);
        });

        return names;
    }

    public Kit getKit(String id) {
        return kits.get(id);
    }

    public boolean setInv(String id, ItemStack[] content) {
        String key = id.toLowerCase();

        if (!kits.containsKey(key))
            return false;

        String path = "kits." + key + ".";

        Kit kit = getKit(key);
        kit.setContent(content);

        config.set(path + "content", content);

        KitsConfig.get().save(config);

        kits.put(key, kit);

        return true;
    }
    public boolean setIcon(String id, Material material) {
        String key = id.toLowerCase();

        if (!kits.containsKey(key))
            return false;

        String path = "kits." + key + ".";

        Kit kit = getKit(key);
        kit.setIcon(material);

        config.set(path + "icon", material.name());

        KitsConfig.get().save(config);

        kits.put(key, kit);

        return true;
    }
    public boolean rename(String id, String name) {
        String key = id.toLowerCase();

        if (!kits.containsKey(key))
            return false;

        String path = "kits." + key + ".";

        Kit kit = getKit(key);
        kit.setDisplayName(name);

        config.set(path + "name", name);
        KitsConfig.get().save(config);

        kits.put(key, kit);

        return true;
    }
    public boolean setEnable(String id, Boolean enable) {
        String key = id.toLowerCase();

        if (!kits.containsKey(key))
            return false;

        String path = "kits." + key + ".";

        Kit kit = getKit(key);
        kit.setEnabled(enable);

        config.set(path + "enabled", enable);
        KitsConfig.get().save(config);

        kits.put(key, kit);

        return true;
    }

    public boolean setRule (String id, String rule, boolean enable) {
        String key = id.toLowerCase();

        if (!kits.containsKey(key))
            return false;

        String path = "kits." + key + ".";

        Kit kit = getKit(key);
        kit.addKitRule(rule, enable);

        config.set(path + "rules."+rule, enable);
        KitsConfig.get().save(config);

        kits.put(key, kit);

        return true;
    }

}
