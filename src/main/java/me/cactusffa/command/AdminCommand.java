package me.cactusffa.command;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.Arena;
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
        if (args[0].equalsIgnoreCase("arena")) {
            return handleArena(player, args);
        }
        plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa [reload|arena]"));
        return true;
    }

    private boolean handleArena(Player player, String[] args) {
        if (args.length < 3) {
            plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa arena <create|setspawn|tp> <id>"));
            return true;
        }
        String action = args[1].toLowerCase(Locale.ROOT);
        String id = args[2].toLowerCase(Locale.ROOT);
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
            default -> {
                plugin.messages().send(player, "invalid-usage", Map.of("usage", "/cacffa arena <create|setspawn|tp> <id>"));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("reload");
            suggestions.add("arena");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("arena")) {
            suggestions.add("create");
            suggestions.add("setspawn");
            suggestions.add("tp");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("arena") && !args[1].equalsIgnoreCase("create")) {
            for (Arena arena : plugin.arenas().all()) {
                suggestions.add(arena.id());
            }
        }
        String needle = args[args.length - 1].toLowerCase(Locale.ROOT);
        return suggestions.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(needle)).toList();
    }
}
