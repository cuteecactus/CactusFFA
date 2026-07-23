package dev.cuteecactus.lobby;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.cuteecactus.utils.ColorUtil;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.color("&cOnly players can use this command"));
            return true;
        }
        
        LobbyManager.get().load(player);
        return true;
    }
    
}
