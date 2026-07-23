package dev.cuteecactus.lobby;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;
import dev.cuteecactus.profile.ProfileState;
import dev.cuteecactus.utils.ColorUtil;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.color("&cOnly players can use this command"));
            return true;
        }

        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null) return true;
        
        if (profile.getProfileState() == ProfileState.IN_FFA) {
            // TODO: leave ffa
        }
        
        player.teleport(LobbyManager.get().getLobby());
        return true;
    }
    
}
