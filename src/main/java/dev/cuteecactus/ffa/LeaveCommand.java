package dev.cuteecactus.ffa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.cuteecactus.config.MessageConfig;
import dev.cuteecactus.lobby.LobbyManager;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;
import dev.cuteecactus.profile.ProfileState;

public class LeaveCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) return true;

        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null) return true;

        if (profile.getProfileState() != ProfileState.IN_FFA) {
            player.sendMessage(MessageConfig.get().getMessage("errors.not-in-ffa"));
            return true;
        }

        LobbyManager.get().load(player);

        return true;
    }
    
}
