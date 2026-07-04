package dev.cuteecactus;

import org.bukkit.plugin.java.JavaPlugin;

public class CactusFFA extends JavaPlugin {

    @Override
    public void onEnable () {
        getLogger().info("Plugin Enabled");
    }
    @Override
    public void onDisable () {
        getLogger().info("Plugin Disabled");
    }
    
}
