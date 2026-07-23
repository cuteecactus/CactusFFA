package dev.cuteecactus.lobby;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import dev.cuteecactus.config.BaseConfig;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;
import dev.cuteecactus.profile.ProfileState;

public class LobbyManager {
    private static LobbyManager instance;
    private FileConfiguration config = BaseConfig.get().getConfig();
    private Location lobbyLocation = null;

    public LobbyManager() {
        instance = this;
        init();
    }

    public static LobbyManager get() {
        return instance;
    }

    private void init() {
        // x y z pitch yaw
        // String world = config.getString("lobby.world");
        // double x = config.getDouble("lobby.x");
        // double y = config.getDouble("lobby.y");
        // double z = config.getDouble("lobby.z");
        // float yaw = (float) config.getDouble("lobby.yaw");
        // float pitch = (float) config.getDouble("lobby.pitch");
        // if (world == null || Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) ||
        // Double.isNaN(yaw) || Double.isNaN(pitch)) {
        // lobbyLocation = null;
        // } else {
        // lobbyLocation = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        // }

        lobbyLocation = config.getLocation("lobby", null);
    }

    public void setLobby(Location location) {
        lobbyLocation = location;
        config.set("lobby", location);
        BaseConfig.get().save(config);
    }

    public Location getLobby() {
        return lobbyLocation;
    }

    public boolean load(Player player) {
        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null)
            return false;

        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        player.setFoodLevel(20);
        player.setHealth(20);

        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(lobbyLocation);

        boolean wasOP = player.isOp();
        player.setOp(true);
        List<String> leaveCommands = config.getStringList("ffa-leave-commands");
        try {
            leaveCommands.forEach(command -> {
                String result = command.replace("{player}", player.getName());
                player.performCommand(result);
            });
        } finally {
            player.setOp(wasOP);
        }

        profile.setProfileState(ProfileState.IN_LOBBY);
        return true;
    }
}
