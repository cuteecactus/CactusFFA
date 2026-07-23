package dev.cuteecactus.ffa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.cuteecactus.config.MessageConfig;
import dev.cuteecactus.profile.ProfileManager;
import dev.cuteecactus.profile.ProfileState;

public class FFACommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) return true; 

        if (!player.hasPermission("cactusffa.use")) {
            player.sendMessage(MessageConfig.get().getMessage("admin.no-permission"));
            return true;
        }

        if (ProfileManager.get().getProfile(player.getUniqueId()).getProfileState() == ProfileState.IN_FFA) {
            player.sendMessage(MessageConfig.get().getMessage("errors.cant-use"));
            return true;
        }

                        
        new FFAGui().open(player);
        return true;
    }

}
