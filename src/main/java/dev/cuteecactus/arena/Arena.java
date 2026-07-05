package dev.cuteecactus.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Arena {
    private final String id;
    private String name;
    private boolean enabled = false;
    private Location corner1;
    private Location corner2;
    private Location spawn;

    public Arena(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public boolean tp(Player player) {
        if (spawn == null) return false;
        player.teleport(spawn);
        return true;
    }
}
