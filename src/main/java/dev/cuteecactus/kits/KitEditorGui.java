package dev.cuteecactus.kits;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import dev.cuteecactus.utils.ColorUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.event.ClickEvent;

public class KitEditorGui {
    public void open(Player player, Kit kit) {
        if (kit == null || player == null)
            return;

        openRootMenu(player, kit);
    }

    void openRootMenu(Player player, Kit kit) {
        Gui gui = Gui.gui()
                .title(ColorUtil.color("&7Kit Editor: " + kit.getDisplayName()))
                .rows(3)
                .disableItemDrop()
                .disableItemPlace()
                .disableItemSwap()
                .disableItemTake()
                .create();

        List<Integer> fillerSlots = List.of(0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);
        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(ColorUtil.color(""))
                .asGuiItem();

        gui.setItem(fillerSlots, filler);

        List<String> itemPreviewLore = List.of();
        GuiItem itemPreview = ItemBuilder.from(kit.getIcon())
                .name(ColorUtil.color("&3" + kit.getDisplayName()))
                .lore(
                        itemPreviewLore.stream()
                                .map(ColorUtil::color)
                                .toList())
                .asGuiItem();

        GuiItem iconItem = ItemBuilder.from(Material.PAINTING)
                .name(ColorUtil.color("&aKit Icon"))
                .lore(
                        List.of("&7Click to change kit icon")
                                .stream()
                                .map(ColorUtil::color)
                                .toList())
                .asGuiItem(e -> {
                    player.closeInventory();
                    sendKitIconPrompt(player, kit.getId());
                });

        GuiItem kitRulesItem = ItemBuilder.from(Material.WRITABLE_BOOK)
                .name(ColorUtil.color("&aKit Rules"))
                .asGuiItem(e -> openKitRulesMenu(player, kit));

        gui.setItem(11, iconItem);
        gui.setItem(4, itemPreview);
        gui.setItem(13, buildKitToggleItem(gui, kit));
        gui.setItem(15, kitRulesItem);
        gui.open(player);
    }

    void openKitRulesMenu(Player player, Kit kit) {
        Gui gui = Gui.gui()
                .title(ColorUtil.color("&7Kit Editor: " + kit.getDisplayName()))
                .rows(3)
                .disableItemDrop()
                .disableItemPlace()
                .disableItemSwap()
                .disableItemTake()
                .create();

        List<Integer> fillerSlots = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);
        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(ColorUtil.color(""))
                .asGuiItem();

        gui.setItem(fillerSlots, filler);


        // Saturation, hunger, drop items, block place, block break
        GuiItem saturationItem = ItemBuilder.from(Material.GOLDEN_APPLE)
        .name(ColorUtil.color("&cAllow Saturation"))
        .lore(
            List.of("&7Allow saturation effect")
            .stream()
            .map(ColorUtil::color)
            .toList()
        )
        .asGuiItem();

        GuiItem hungerItem = ItemBuilder.from(Material.COOKED_BEEF)
        .name(ColorUtil.color("&cAllow Hunger"))
        .lore(
            List.of("&7Allow hunger effect")
            .stream()
            .map(ColorUtil::color)
            .toList()
        )
        .asGuiItem();

        GuiItem dropItemsItem = ItemBuilder.from(Material.ELYTRA)
        .name(ColorUtil.color("&cAllow Hunger"))
        .lore(
            List.of("&7Allow droping inventory on player death")
            .stream()
            .map(ColorUtil::color)
            .toList()
        )
        .asGuiItem();

        GuiItem breakBlockItem = ItemBuilder.from(Material.IRON_PICKAXE)
        .name(ColorUtil.color("&cAllow Breaking Block"))
        .lore(
            List.of("&7Allow players to break blocks placed by players")
            .stream()
            .map(ColorUtil::color)
            .toList()
        )
        .asGuiItem();

        GuiItem placeBlockItem = ItemBuilder.from(Material.GRASS_BLOCK)
        .name(ColorUtil.color("&cAllow Placing Block"))
        .lore(
            List.of("&7Allow players to place blocks")
            .stream()
            .map(ColorUtil::color)
            .toList()
        )
        .asGuiItem();
        
        
        gui.addItem(saturationItem);
        gui.addItem(hungerItem);
        gui.addItem(dropItemsItem);
        gui.addItem(placeBlockItem);
        gui.addItem(breakBlockItem);
        gui.open(player);

    }

    void sendKitIconPrompt(Player player, String id) {
        player.sendMessage(ColorUtil.color("&a&lClick to set kit icon")
                .clickEvent(ClickEvent.suggestCommand("/cffa kit icon " + id + " ")));

    }

    GuiItem buildKitToggleItem(Gui gui, Kit kit) {
        Material itemMaterial = kit.getEnabled() ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
        String name = kit.getEnabled() ? "&aEnabled" : "&cDisabled";

        GuiItem item = ItemBuilder.from(itemMaterial)
                .name(ColorUtil.color(name))
                .asGuiItem(e -> {
                    KitManager.get().setEnable(kit.getId(), !kit.getEnabled());
                    gui.setItem(13, buildKitToggleItem(gui, kit));
                    gui.update();
                });

        return item;
    }
}
