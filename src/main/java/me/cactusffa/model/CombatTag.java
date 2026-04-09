package me.cactusffa.model;

import java.util.UUID;

public final class CombatTag {

    private final UUID owner;
    private UUID opponent;
    private long expiresAt;

    public CombatTag(UUID owner, UUID opponent, long expiresAt) {
        this.owner = owner;
        this.opponent = opponent;
        this.expiresAt = expiresAt;
    }

    public UUID owner() {
        return owner;
    }

    public UUID opponent() {
        return opponent;
    }

    public void opponent(UUID opponent) {
        this.opponent = opponent;
    }

    public long expiresAt() {
        return expiresAt;
    }

    public void expiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
