package dev.cuteecactus;

import dev.cuteecactus.arena.ArenaManager;
import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.kits.KitManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CactusFFACommandTabCompleter implements TabCompleter {

    private static final List<String> ROOT = Arrays.asList("kit", "arena", "lobby");

    private static final Map<String, List<String>> KIT_SUB = Map.of(
            "create", List.of("<name>"),
            "delete", List.of(),
            "setinv", List.of(),
            "icon", List.of(),
            "load", List.of(),
            "editor", List.of(),
            "rename", List.of(),
            "breakableblocks", List.of());

    private static final Map<String, List<String>> ARENA_SUB = Map.of(
            "create", List.of("<name>"),
            "delete", List.of(),
            "spawn", List.of(),
            "corner1", List.of(),
            "corner2", List.of(),
            "tp", List.of(),
            "rename", List.of(),
            "enable", List.of());

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (args.length == 1) {
            return filter(ROOT, args[0]);
        }

        String root = args[0].toLowerCase();

        if (args.length == 2) {
            if (root.equals("kit")) {
                return filter(KIT_SUB.keySet().stream().toList(), args[1]);
            }
            if (root.equals("arena")) {
                return filter(ARENA_SUB.keySet().stream().toList(), args[1]);
            }
            return Collections.emptyList();
        }

        if (args.length == 3) {
            String sub = args[1].toLowerCase();

            if (root.equals("kit")) {
                if (sub.equals("create")) {
                    return List.of("<name>");
                }
                if (Arrays.asList("setinv", "icon", "load", "editor", "rename", "delete", "breakableblocks").contains(sub)) {
                    return filter(KitManager.get().getAllNames(), args[2]);
                }
            }

            if (root.equals("arena")) {
                if (sub.equals("create")) {
                    return List.of("<name>");
                }

                if (Arrays.asList("spawn", "corner1", "corner2", "tp", "delete", "enable", "rename").contains(sub)) {

                    return filter(ArenaManager.get().getAllArenaNames(), args[2]);
                }
            }
        }

        if (args.length == 4) {
            String sub = args[1].toLowerCase();
            if (root.equals("kit")) {
                if (sub.equals("rename")) {
                    return List.of("<name>");
                }
                if (sub.equals("icon")) {
                    Material[] materials = Material.values();
                    List <String> result = new ArrayList<>();
                    for (Material material : materials) {
                        result.add(material.name());
                    }
                    return result;
                }
                if (sub.equals("breakableblocks")) {
                    return List.of("add", "remove", "list");
                }
            }
            if (root.equals("arena")) {
                if (sub.equals("enable")) {
                    return List.of("true", "false");
                }
                if (sub.equals("rename")) {
                    return List.of("<name>");
                }
            }
        }

        if (args.length == 5) {
            String sub = args[1].toLowerCase();
            if (root.equals("kit") && sub.equals("breakableblocks")) {
                String kitId = args[2];
                Kit kit = KitManager.get().getKit(kitId);
                if (kit != null && args[3].equalsIgnoreCase("remove")) {
                    return kit.getBreakableBlocks().stream()
                            .map(Material::name)
                            .toList();
                }
                if (args[3].equalsIgnoreCase("add")) {
                    Material[] materials = Material.values();
                    List<String> result = new ArrayList<>();
                    for (Material material : materials) {
                        result.add(material.name());
                    }
                    return result;
                }
            }
        }

        return Collections.emptyList();
    }

    private List<String> filter(Collection<String> input, String prefix) {
        List<String> result = new ArrayList<>();
        for (String s : input) {
            if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }
}