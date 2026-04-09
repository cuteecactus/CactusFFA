package me.cactusffa.model;

import java.util.UUID;

public final class PlayerSession {

    private final UUID playerId;
    private final PlayerSnapshot snapshot;
    private String currentArenaId;
    private String currentKitId;
    private int kills;
    private int killStreak;

    public PlayerSession(UUID playerId, PlayerSnapshot snapshot) {
        this.playerId = playerId;
        this.snapshot = snapshot;
    }

    public UUID playerId() {
        return playerId;
    }

    public PlayerSnapshot snapshot() {
        return snapshot;
    }

    public String currentArenaId() {
        return currentArenaId;
    }

    public void currentArenaId(String currentArenaId) {
        this.currentArenaId = currentArenaId;
    }

    public String currentKitId() {
        return currentKitId;
    }

    public void currentKitId(String currentKitId) {
        this.currentKitId = currentKitId;
    }

    public int kills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
        this.killStreak++;
    }

    public int killStreak() {
        return killStreak;
    }

    public void resetKillStreak() {
        this.killStreak = 0;
    }
}
