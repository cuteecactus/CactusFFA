package dev.cuteecactus.command;

import dev.cuteecactus.CactusFFAPlugin;
import dev.cuteecactus.model.Arena;
import dev.cuteecactus.model.KitCategory;
import dev.cuteecactus.model.KitDefinition;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class FfaCommand implements CommandExecutor, TabCompleter {

    private final CactusFFAPlugin plugin;

    public FfaCommand(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().send(sender, "only-players");
            return true;
        }

        if (args.length == 0) {
            String defaultCategory = plugin.getConfig().getString("ffa.default-category-on-command", "");
            if (defaultCategory != null && !defaultCategory.isBlank()) {
                plugin.kits().category(defaultCategory).ifPresentOrElse(category -> plugin.menus().openCategory(player, category),
                        () -> plugin.menus().openRoot(player));
                return true;
            }
            plugin.menus().openRoot(player);
            return true;
        }

        Optional<KitDefinition> kit = plugin.kits().resolve(args);
        if (kit.isPresent()) {
            return joinKit(player, kit.get());
        }

        Optional<KitCategory> category = plugin.kits().category(args[0]);
        if (category.isPresent() && args.length == 1) {
            plugin.menus().openCategory(player, category.get());
            plugin.messages().send(player, "category-opened", Map.of("category", category.get().displayName()));
            return true;
        }

        plugin.messages().send(player, "unknown-ffa-target");
        return true;
    }

    private boolean joinKit(Player player, KitDefinition kit) {
        if (plugin.combat().isTagged(player.getUniqueId())) {
            plugin.messages().send(player, "combat-blocked");
            return true;
        }
        if (!kit.permission().isBlank() && !player.hasPermission(kit.permission())) {
            plugin.messages().send(player, "menu-no-access");
            return true;
        }

        Optional<Arena> arena = plugin.arenas().find(kit.arenaId());
        if (arena.isEmpty()) {
            plugin.messages().send(player, "arena-missing", Map.of("arena", kit.arenaId()));
            return true;
        }

        plugin.sessions().join(player, kit, arena.get());
        plugin.messages().send(player, "joined-kit", Map.of("kit", kit.displayName(), "arena", arena.get().displayName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (KitCategory category : plugin.kits().categories()) {
                suggestions.add(category.id());
            }
            for (KitDefinition kit : plugin.kits().kits()) {
                suggestions.add(kit.id());
            }
        } else if (args.length == 2) {
            plugin.kits().category(args[0]).ifPresent(category -> plugin.kits().kitsInCategory(category.id()).forEach(kit -> {
                String[] parts = kit.id().split("_", 2);
                if (parts.length == 2) {
                    suggestions.add(parts[1]);
                }
            }));
        }
        String needle = args[args.length - 1].toLowerCase(Locale.ROOT);
        return suggestions.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(needle)).distinct().toList();
    }
}
