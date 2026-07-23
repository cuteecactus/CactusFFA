package dev.cuteecactus.ffa;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import dev.cuteecactus.arena.Arena;
import dev.cuteecactus.config.MessageConfig;
import dev.cuteecactus.config.gui.FFAGuiConfig;
import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.kits.KitManager;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;
import dev.cuteecactus.profile.ProfileState;
import dev.cuteecactus.utils.ColorUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;

public class FFAGui {
    private FileConfiguration config = FFAGuiConfig.get().getConfig();

    public void open(Player player) {
        if (config == null) {
            player.sendMessage(ColorUtil.color("&cFFA Gui config not found."));
            return;
        }

        openFfaGui(player);
    }

    private void openFfaGui(Player player) {
        Gui gui = Gui.gui()
                .title(ColorUtil.color(config.getString("ffa-gui.title")))
                .rows(config.getInt("ffa-gui.rows"))
                .disableItemDrop()
                .disableItemPlace()
                .disableItemSwap()
                .disableItemTake()
                .create();

        GuiItem filler = ItemBuilder.from(Material.matchMaterial(config.getString("ffa-gui.filler.material")))
                .name(ColorUtil.color(config.getString("ffa-gui.filler.name")))
                .asGuiItem();

        for (int slot : config.getIntegerList("ffa-gui.filler.slots")) {
            gui.setItem(slot, filler);
        }

        for (String kitId : config.getConfigurationSection("ffa-gui.kits").getKeys(false)) {
            Kit kit = KitManager.get().getKit(kitId);
            if (kit == null)
                continue;

            GuiItem kitItem = ItemBuilder.from(kit.getIcon())
                    .name(ColorUtil
                            .color(config.getString("ffa-gui.kit-display.name").replace("{kit}", kit.getDisplayName())))
                    .lore(
                            config.getStringList("ffa-gui.kit-display.lore")
                                    .stream()
                                    .map(line -> line.replace("{kit}", kit.getDisplayName()))
                                    .map(ColorUtil::color)
                                    .toList())
                    .asGuiItem(e -> {
                        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
                        if (profile == null )return;
                        
                        
                        Arena arena = kit.getArena();
                        if (arena == null) {
                            player.sendMessage(MessageConfig.get().getMessage("errors.no-arena"));
                            return;
                        }

                        player.teleport(arena.getSpawn());
                        player.getInventory().clear();
                        player.getActivePotionEffects().clear();
                        player.getInventory().setContents(kit.getContent());
                        profile.setCurrentArena(arena);
                        profile.setCurrentKit(kit);
                        profile.setProfileState(ProfileState.IN_FFA);

                    });

            gui.setItem(config.getInt("ffa-gui.kits." + kitId), kitItem);
        }

        gui.open(player);
    }
}
