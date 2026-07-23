package dev.cuteecactus.lobby;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import dev.cuteecactus.config.BaseConfig;

public class LobbyManager {
    private static LobbyManager instance;
    private FileConfiguration config = BaseConfig.get().getConfig();
    private Location lobbyLocation = null;

    public LobbyManager () {
        instance = this;
        init();
    }

    public static LobbyManager get () {
        return instance;
    }

    private void init () {
        // x y z pitch yaw
        // String world = config.getString("lobby.world");
        // double x = config.getDouble("lobby.x");
        // double y = config.getDouble("lobby.y");
        // double z = config.getDouble("lobby.z");
        // float yaw = (float) config.getDouble("lobby.yaw");
        // float pitch = (float) config.getDouble("lobby.pitch");
        // if (world == null || Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || Double.isNaN(yaw) || Double.isNaN(pitch)) {
        //     lobbyLocation = null;
        // } else {
        //     lobbyLocation = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        // }

        lobbyLocation = config.getLocation("lobby", null);
    }

    public void setLobby (Location location) {
        config.getLocation("lobby");
        lobbyLocation = location;
    }

    public Location getLobby () {
        return lobbyLocation;
    }
}
