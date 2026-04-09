package me.cactusffa.command;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.Arena;
import me.cactusffa.model.KitCategory;
import me.cactusffa.model.KitDefinition;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class AdminCommand implements CommandExecutor, TabCompleter {

    private final CactusFFAPlugin plugin;

    public AdminCommand(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().send(sender, "only-players");
            return true;
        }
        if (!player.hasPermission("cactusffa.admin")) {
            plugin.messages().send(player, "no-permission");
            return true;
        }
        if (args.length == 0) {
            plugin.menus().openAdmin(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPlugin();
            plugin.messages().send(player, "config-reloaded");
            return true;
        }
        if (args[0].equalsIgnoreCase("setlobby")) {
            plugin.worlds().setMainLobby(player.getLocation());
            plugin.messages().send(player, "lobby-set");
            return true;
        }
        if (args[0].equalsIgnoreCase("arena")) {
            return handleArena(player, args);
        }
        if (args[0].equalsIgnoreCase("kitcategory")) {
            return handleKitCategory(player, args);
        }
        if (args[0].equalsIgnoreCase("kit")) {
            return handleKit(player, args);
        }
        plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa [reload|setlobby|arena|kitcategory|kit]"));
        return true;
    }

    private boolean handleArena(Player player, String[] args) {
        if (args.length < 3) {
            plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa arena <create|setspawn|tp|delete> <id>"));
            return true;
        }
        String action = args[1].toLowerCase(Locale.ROOT);
        String id = plugin.kits().normalizeId(args[2]);
        if ((action.equals("create") || action.equals("setspawn")) && !plugin.worlds().isArenaWorld(player.getWorld())) {
            plugin.messages().send(player, "must-be-in-world", Map.of("world", plugin.worlds().arenaWorldName()));
            return true;
        }
        switch (action) {
            case "create" -> {
                plugin.arenas().saveArena(id, player.getLocation());
                plugin.messages().send(player, "arena-created", Map.of("arena", id));
                return true;
            }
            case "setspawn" -> {
                plugin.arenas().saveArena(id, player.getLocation());
                plugin.messages().send(player, "arena-updated", Map.of("arena", id));
                return true;
            }
            case "tp" -> {
                Arena arena = plugin.arenas().find(id).orElse(null);
                if (arena == null) {
                    plugin.messages().send(player, "arena-missing", Map.of("arena", id));
                    return true;
                }
                player.teleport(arena.spawn());
                plugin.messages().send(player, "arena-teleported", Map.of("arena", id));
                return true;
            }
            case "delete" -> {
                if (!plugin.arenas().deleteArena(id)) {
                    plugin.messages().send(player, "arena-missing", Map.of("arena", id));
                    return true;
                }
                plugin.messages().send(player, "arena-deleted", Map.of("arena", id));
                return true;
            }
            default -> {
                plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa arena <create|setspawn|tp|delete> <id>"));
                return true;
            }
        }
    }

    private boolean handleKitCategory(Player player, String[] args) {
        if (args.length < 3) {
            plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa kitcategory <create|delete> <id>"));
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        String id = plugin.kits().normalizeId(args[2]);
        if (id.isBlank()) {
            plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa kitcategory <create|delete> <id>"));
            return true;
        }
        if (action.equals("create")) {
            if (plugin.kits().category(id).isPresent()) {
                plugin.messages().send(player, "category-exists", Map.of("category", id));
                return true;
            }
            if (!plugin.kits().createCategory(id)) {
                plugin.messages().send(player, "save-failed");
                return true;
            }
            plugin.messages().send(player, "category-created", Map.of("category", id));
            return true;
        }
        if (action.equals("delete")) {
            if (plugin.kits().category(id).isEmpty()) {
                plugin.messages().send(player, "category-missing", Map.of("category", id));
                return true;
            }
            if (plugin.kits().hasKitsInCategory(id)) {
                plugin.messages().send(player, "category-not-empty", Map.of("category", id));
                return true;
            }
            if (!plugin.kits().deleteCategory(id)) {
                plugin.messages().send(player, "save-failed");
                return true;
            }
            plugin.messages().send(player, "category-deleted", Map.of("category", id));
            return true;
        }
        plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa kitcategory <create|delete> <id>"));
        return true;
    }

    private boolean handleKit(Player player, String[] args) {
        if (args.length < 3) {
            plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa kit <create|setinventory|delete> ..."));
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        switch (action) {
            case "create" -> {
                if (args.length < 4) {
                    plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa kit create <id> <arena> [category|none]"));
                    return true;
                }

                String id = plugin.kits().normalizeId(args[2]);
                String arenaId = plugin.kits().normalizeId(args[3]);
                String categoryId = args.length >= 5 ? plugin.kits().normalizeOptionalId(args[4]) : "";

                if (plugin.kits().kit(id).isPresent()) {
                    plugin.messages().send(player, "kit-exists", Map.of("kit", id));
                    return true;
                }
                if (plugin.arenas().find(arenaId).isEmpty()) {
                    plugin.messages().send(player, "arena-missing", Map.of("arena", arenaId));
                    return true;
                }
                if (!categoryId.isBlank() && plugin.kits().category(categoryId).isEmpty()) {
                    plugin.messages().send(player, "category-missing", Map.of("category", categoryId));
                    return true;
                }

                Material icon = player.getInventory().getItemInMainHand().getType().isAir()
                        ? Material.CHEST
                        : player.getInventory().getItemInMainHand().getType();
                if (!plugin.kits().createKit(id, arenaId, categoryId, icon)) {
                    plugin.messages().send(player, "save-failed");
                    return true;
                }
                plugin.messages().send(player, "kit-created", Map.of("kit", id, "arena", arenaId));
                return true;
            }
            case "setinventory" -> {
                String id = plugin.kits().normalizeId(args[2]);
                KitDefinition kit = plugin.kits().kit(id).orElse(null);
                if (kit == null) {
                    plugin.messages().send(player, "kit-missing", Map.of("kit", id));
                    return true;
                }
                if (!plugin.kits().setKitInventory(
                        id,
                        player.getInventory().getContents(),
                        player.getInventory().getArmorContents(),
                        player.getInventory().getExtraContents())) {
                    plugin.messages().send(player, "save-failed");
                    return true;
                }
                plugin.messages().send(player, "kit-inventory-updated", Map.of("kit", id));
                return true;
            }
            case "delete" -> {
                String id = plugin.kits().normalizeId(args[2]);
                if (plugin.kits().kit(id).isEmpty()) {
                    plugin.messages().send(player, "kit-missing", Map.of("kit", id));
                    return true;
                }
                if (!plugin.kits().deleteKit(id)) {
                    plugin.messages().send(player, "save-failed");
                    return true;
                }
                plugin.messages().send(player, "kit-deleted", Map.of("kit", id));
                return true;
            }
            default -> {
                plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa kit <create|setinventory|delete> ..."));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("reload");
            suggestions.add("setlobby");
            suggestions.add("arena");
            suggestions.add("kitcategory");
            suggestions.add("kit");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("arena")) {
            suggestions.add("create");
            suggestions.add("setspawn");
            suggestions.add("tp");
            suggestions.add("delete");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("kitcategory")) {
            suggestions.add("create");
            suggestions.add("delete");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("kit")) {
            suggestions.add("create");
            suggestions.add("setinventory");
            suggestions.add("delete");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("arena") && !args[1].equalsIgnoreCase("create")) {
            for (Arena arena : plugin.arenas().all()) {
                suggestions.add(arena.id());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("kitcategory") && args[1].equalsIgnoreCase("delete")) {
            for (KitCategory category : plugin.kits().categories()) {
                suggestions.add(category.id());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("setinventory")) {
            for (KitDefinition kit : plugin.kits().kits()) {
                suggestions.add(kit.id());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("delete")) {
            for (KitDefinition kit : plugin.kits().kits()) {
                suggestions.add(kit.id());
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("create")) {
            for (Arena arena : plugin.arenas().all()) {
                suggestions.add(arena.id());
            }
        } else if (args.length == 5 && args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("create")) {
            suggestions.add("none");
            for (KitCategory category : plugin.kits().categories()) {
                suggestions.add(category.id());
            }
        }
        String needle = args[args.length - 1].toLowerCase(Locale.ROOT);
        return suggestions.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(needle)).toList();
    }
}
