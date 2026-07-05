package dev.cuteecactus;

import org.bukkit.plugin.java.JavaPlugin;

import dev.cuteecactus.arena.ArenaManager;
import dev.cuteecactus.config.ConfigManager;
import dev.cuteecactus.ffa.FFACommand;
import dev.cuteecactus.kits.KitManager;

public class CactusFFA extends JavaPlugin {

    private static CactusFFA instance;

    private CactusFFA() {
        instance = this;
    }

    public static CactusFFA get() {
        return instance;
    }

    @Override
    public void onEnable() {
        registerCommands();

        new ConfigManager();
        new KitManager();
        new ArenaManager();

        getLogger().info("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled");
    }

    private void registerCommands() {
        getCommand("cactusffa").setExecutor(new CactusFFACommand());
        getCommand("cactusffa").setTabCompleter(new CactusFFACommandTabCompleter());;

        getCommand("ffa").setExecutor(new FFACommand());
    }

}
