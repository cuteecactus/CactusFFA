package dev.cuteecactus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cuteecactus.arena.ArenaManager;
import dev.cuteecactus.config.BaseConfig;
import dev.cuteecactus.config.ConfigManager;
import dev.cuteecactus.ffa.FFACommand;
import dev.cuteecactus.ffa.LeaveCommand;
import dev.cuteecactus.kits.KitManager;
import dev.cuteecactus.listeners.BlockBreakListener;
import dev.cuteecactus.listeners.BlockPlaceListener;
import dev.cuteecactus.listeners.DeathListener;
import dev.cuteecactus.listeners.FoodLevelChangeListener;
import dev.cuteecactus.listeners.JoinListener;
import dev.cuteecactus.listeners.LeaveListener;
import dev.cuteecactus.listeners.RegainHealthListener;
import dev.cuteecactus.lobby.LobbyCommand;
import dev.cuteecactus.lobby.LobbyManager;
import dev.cuteecactus.profile.ProfileManager;

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
        registerListeners();
        
        new ConfigManager();
        new KitManager();
        new ArenaManager();
        new ProfileManager();
        new LobbyManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            ProfileManager.get().addProfile(player.getUniqueId());
        }

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
        getCommand("leaveffa").setExecutor(new LeaveCommand()); 

        if (BaseConfig.get().getConfig().getBoolean("lobby-command") == true) {
            getCommand("lobby").setExecutor(new LobbyCommand());
        }
    }

    private void registerListeners () {
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);
        getServer().getPluginManager().registerEvents(new RegainHealthListener(), this);
        getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
    }

}
