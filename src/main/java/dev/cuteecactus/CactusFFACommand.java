package dev.cuteecactus;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.cuteecactus.arena.Arena;
import dev.cuteecactus.arena.ArenaManager;
import dev.cuteecactus.config.MessageConfig;
import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.kits.KitEditorGui;
import dev.cuteecactus.kits.KitManager;
import dev.cuteecactus.lobby.LobbyManager;
import dev.cuteecactus.utils.ColorUtil;

public class CactusFFACommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.color("&cOnly players can use this command"));
            return true;
        }
        
        if (!player.hasPermission("cactusffa.admin")) {
            player.sendMessage(MessageConfig.get().getMessage("admin.no-permission"));
            return true;
        }

        if (args.length == 0) {
            // TODO: admin panel
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if (subCommand == null)
            return true;

        switch (subCommand) {
            // /cffa kit
            case "kit":
                handleKitsCommand(args, player);
                break;
            // /cffa arena
            case "arena":
                handleArenaCommand(args, player);
                break;
            // /cffa lobby
            case "setlobby":
                LobbyManager.get().setLobby(player.getLocation());
            // /cffa *
            default:
                // TODO: send help
                break;
        }

        return true;
    }

    private void handleKitsCommand(String[] args, Player player) {

        if (args.length == 1) {
            player.sendMessage(MessageConfig.get().getMessage("command-usage.kit"));
            return;
        }

        String action = args[1];
        // /cffa kit create <id>
        if (action.equalsIgnoreCase("create")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.kit-create"));
                return;
            }

            String kitId = args[2];

            if (KitManager.get().createKit(kitId, player)) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-created"));
            }
            return;
        }
        // /cffa kit setinv <id>
        if (action.equalsIgnoreCase("setinv")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.kit-setinv"));
                return;
            }

            String kitId = args[2];
            Kit kit = KitManager.get().getKit(kitId);

            if (kit == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-not-found", "{kit}", kitId));
                return;
            }

            if (KitManager.get().setInv(kitId, player.getInventory().getContents())) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-edited", "{kit}", kitId));
            } else {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-error", "{kit}", kitId));

            }
        }
        // /cffa kit load <id>
        if (action.equalsIgnoreCase("load")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.kit-load"));
                return;
            }

            String kitId = args[2];
            Kit kit = KitManager.get().getKit(kitId);

            if (kit == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-not-found", "{kit}", kitId));
                return;
            }

            kit.applyContent(player);
            player.sendMessage(MessageConfig.get().getMessage("admin.kit-loaded"));
        }

        // /cffa kit icon <id> <material>
        if (action.equalsIgnoreCase("icon")) {
            if (args.length != 4) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.kit-icon"));
                return;
            }

            String kitId = args[2];
            Kit kit = KitManager.get().getKit(kitId);
            if (kit == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-not-found", "{kit}", kitId));
                return;
            }

            String iconString = args[3];
            Material icon = Material.matchMaterial(iconString);

            if (icon == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.incorrect-icon"));
                return;
            }

            KitManager.get().setIcon(kitId, icon);
            player.sendMessage(MessageConfig.get().getMessage("admin.kit-edited"));
        }

        // /cffa kit rename <id> <new name>
        if (action.equalsIgnoreCase("rename")) {
            if (args.length != 4) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.kit-rename"));
                return;
            }

            String kitId = args[2];
            Kit kit = KitManager.get().getKit(kitId);
            String name = args[3];

            if (kit == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-not-found", "{kit}", kitId));
            }

            if (KitManager.get().rename(kitId, name)) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-renamed", "{kit}", kitId, "{name}", name));
            }
            return;
        }

        // /cffa kit editor <id>
        if (action.equalsIgnoreCase("editor")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.kit-editor"));
                return;
            }

            String kitId = args[2];
            Kit kit = KitManager.get().getKit(kitId);

            if (kit == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.kit-not-found", "{kit}", kitId));
            }

            new KitEditorGui().open(player, kit);
            return;
        }

    }

    private void handleArenaCommand(String[] args, Player player) {
        if (args.length == 1) {
            player.sendMessage(MessageConfig.get().getMessage("command-usage.arena"));
            return;
        }

        String action = args[1];

        // /cffa arena create <id>
        if (action.equalsIgnoreCase("create")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.arena-create"));
                return;
            }

            String arenaId = args[2];

            if (ArenaManager.get().createArena(arenaId)) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-created"));
            }
            return;
        }

        // /cffa arena enable <id> <true/false>
        if (action.equalsIgnoreCase("enable")) {
            if (args.length != 4) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.arena-enable"));
                return;
            }

            String arenaId = args[2];
            boolean enable = Boolean.parseBoolean(args[3]);
            Arena arena = ArenaManager.get().getArena(arenaId);

            if (arena == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-not-found", "{arena}", arenaId));
            }

            if (ArenaManager.get().setEnabled(arenaId, enable)) {
                if (enable == true
                        && (arena.getCorner1() == null || arena.getCorner2() == null || arena.getSpawn() == null)) {
                    player.sendMessage(MessageConfig.get().getMessage("admin.arena-cant-enable"));
                    return;
                }

                player.sendMessage(
                        MessageConfig.get().getMessage(enable ? "admin.arena-enabled" : "admin.arena-disabled"));
            }
            return;
        }

        // /cffa arena corner1 <id>
        if (action.equalsIgnoreCase("corner1")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.arena-corner1"));
                return;
            }

            String arenaId = args[2];
            Arena arena = ArenaManager.get().getArena(arenaId);
            Location location = player.getLocation();

            if (arena == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-not-found", "{arena}", arenaId));
            }

            if (ArenaManager.get().setCorner1(arenaId, location)) {

                player.sendMessage(MessageConfig.get().getMessage("admin.arena-corner1-set", "{arena}", arenaId));
            }
            return;
        }

        // /cffa arena corner2 <id>
        if (action.equalsIgnoreCase("corner2")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.arena-corner2"));
                return;
            }

            String arenaId = args[2];
            Arena arena = ArenaManager.get().getArena(arenaId);
            Location location = player.getLocation();

            if (arena == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-not-found", "{arena}", arenaId));
            }

            if (ArenaManager.get().setCorner2(arenaId, location)) {

                player.sendMessage(MessageConfig.get().getMessage("admin.arena-corner2-set", "{arena}", arenaId));
            }
            return;
        }

        // /cffa arena spawn <id>
        if (action.equalsIgnoreCase("spawn")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.arena-spawn"));
                return;
            }

            String arenaId = args[2];
            Arena arena = ArenaManager.get().getArena(arenaId);
            Location location = player.getLocation();

            if (arena == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-not-found", "{arena}", arenaId));
            }

            if (ArenaManager.get().setSpawn(arenaId, location)) {

                player.sendMessage(MessageConfig.get().getMessage("admin.arena-spawn-set", "{arena}", arenaId));
            }
            return;
        }

        // /cffa arena rename <id> <new name>
        if (action.equalsIgnoreCase("rename")) {
            if (args.length != 4) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.arena-rename"));
                return;
            }

            String arenaId = args[2];
            Arena arena = ArenaManager.get().getArena(arenaId);
            String name = args[3];

            if (arena == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-not-found", "{arena}", arenaId));
            }

            if (ArenaManager.get().rename(arenaId, name)) {

                player.sendMessage(
                        MessageConfig.get().getMessage("admin.arena-renamed", "{arena}", arenaId, "{name}", name));
            }
            return;
        }

        // /cffa arena tp <id>        
        if (action.equalsIgnoreCase("tp")) {
            if (args.length != 3) {
                player.sendMessage(MessageConfig.get().getMessage("command-usage.arena-tp"));
                return;
            }

            String arenaId = args[2];
            Arena arena = ArenaManager.get().getArena(arenaId);

            if (arena == null) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-not-found", "{arena}", arenaId));
            }

            if (arena.tp(player)) {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-tped", "{arena}", arenaId));
            } else {
                player.sendMessage(MessageConfig.get().getMessage("admin.arena-error-teleporting", "{arena}", arenaId));
            }
            return;
        }
    }
}
