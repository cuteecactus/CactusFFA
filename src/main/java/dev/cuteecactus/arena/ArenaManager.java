package dev.cuteecactus.arena;

public class ArenaManager {
    private static ArenaManager instance;

    public ArenaManager() {
        instance = this;
    }

    public static ArenaManager get() {
        return instance;
    }

    private void loadArenas() {
        
    }
}
