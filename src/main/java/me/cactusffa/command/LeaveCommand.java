package me.cactusffa.command;

import me.cactusffa.CactusFFAPlugin;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LeaveCommand implements CommandExecutor {

    private final CactusFFAPlugin plugin;

    public LeaveCommand(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().send(sender, "only-players");
            return true;
        }
        if (!plugin.sessions().isInFfa(player)) {
            plugin.messages().send(player, "not-in-ffa");
            return true;
        }
        if (plugin.combat().isTagged(player.getUniqueId())) {
            plugin.messages().send(player, "combat-blocked");
            return true;
        }
        plugin.sessions().leave(player);
        plugin.messages().send(player, "left-ffa");
        try {
            player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.leave-ffa", "ENTITY_ENDERMAN_TELEPORT")), 1.0F, 1.0F);
        } catch (IllegalArgumentException ignored) {
        }
        return true;
    }
}
