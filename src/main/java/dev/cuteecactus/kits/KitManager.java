package dev.cuteecactus.kits;

import java.util.Arrays;
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

            String displayName = config.getString(path + "display-name");

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

    public boolean createKit (String id, Player player) {
        if (id == null || player == null || kits.contains(id.toLowerCase())) return false;

        Kit kit = new Kit(id);
        kit.setDisplayName(id);
        kit.setContent(player.getInventory().getContents());
        kit.setEnabled(true);

        String path = "kits." + id + ".";
        
        config.set(path + "display-name", kit.getDisplayName());
        config.set(path + "enabled", kit.getEnabled());
        config.set(path + "icon", kit.getIcon().name());
        config.set(path + "content", kit.getContent());
        
        KitsConfig.get().save(config);
        return true;
    }

    public Set<String> getAllNames () {
        Set<String> names = ConcurrentHashMap.newKeySet();
        kits.forEach((k,v) -> {
            names.add(k);
        });

        return names;
    }

}
