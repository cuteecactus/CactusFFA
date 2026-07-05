package dev.cuteecactus.ffa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.cuteecactus.config.MessageConfig;

public class FFACommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) return false; 

        if (!player.hasPermission("cactusffa.use")) {
            player.sendMessage(MessageConfig.get().getMessage("admin.no-permission"));
            return false;
        }
                        
        new FFAGui().open(player);
        return true;
    }

}
