package me.cactusffa;

import me.cactusffa.command.AdminCommand;
import me.cactusffa.command.FfaCommand;
import me.cactusffa.command.LeaveCommand;
import me.cactusffa.listener.CombatListener;
import me.cactusffa.listener.GuiListener;
import me.cactusffa.listener.ProtectionListener;
import me.cactusffa.listener.SessionListener;
import me.cactusffa.menu.MenuManager;
import me.cactusffa.scoreboard.ScoreboardService;
import me.cactusffa.service.ArenaManager;
import me.cactusffa.service.CombatManager;
import me.cactusffa.service.KitManager;
import me.cactusffa.service.PlayerSessionManager;
import me.cactusffa.service.WorldService;
import me.cactusffa.util.MessageService;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CactusFFAPlugin extends JavaPlugin {

    private MessageService messageService;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private PlayerSessionManager playerSessionManager;
    private CombatManager combatManager;
    private ScoreboardService scoreboardService;
    private MenuManager menuManager;
    private WorldService worldService;

    @Override
    public void onEnable() {
        updateConfigs();

        this.messageService = new MessageService(this);
        this.worldService = new WorldService(this);
        this.worldService.prepareArenaWorld();

        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);
        this.playerSessionManager = new PlayerSessionManager(this);
        this.combatManager = new CombatManager(this);
        this.scoreboardService = new ScoreboardService(this);
        this.menuManager = new MenuManager(this);

        this.arenaManager.reload();
        this.kitManager.reload();
        this.menuManager.reload();

        registerCommands();
        registerListeners();

        this.combatManager.startTask();
        this.scoreboardService.startTask();
    }

    @Override
    public void onDisable() {
        if (combatManager != null) {
            combatManager.shutdown();
        }
        if (scoreboardService != null) {
            scoreboardService.shutdown();
        }
        if (playerSessionManager != null) {
            playerSessionManager.restoreAll();
        }
    }

    public void reloadPlugin() {
        updateConfigs();
        messageService.reload();
        worldService.prepareArenaWorld();
        arenaManager.reload();
        kitManager.reload();
        menuManager.reload();
        combatManager.reload();
        scoreboardService.reload();
    }

    public MessageService messages() {
        return messageService;
    }

    public ArenaManager arenas() {
        return arenaManager;
    }

    public KitManager kits() {
        return kitManager;
    }

    public PlayerSessionManager sessions() {
        return playerSessionManager;
    }

    public CombatManager combat() {
        return combatManager;
    }

    public ScoreboardService scoreboard() {
        return scoreboardService;
    }

    public MenuManager menus() {
        return menuManager;
    }

    public WorldService worlds() {
        return worldService;
    }

    private void registerCommands() {
        register("ffa", new FfaCommand(this));
        register("leave", new LeaveCommand(this));
        register("cacffa", new AdminCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new SessionListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
    }

    private void register(String label, Object executor) {
        PluginCommand command = getCommand(label);
        if (command == null) {
            throw new IllegalStateException("Command not defined in plugin.yml: " + label);
        }
        if (executor instanceof org.bukkit.command.CommandExecutor commandExecutor) {
            command.setExecutor(commandExecutor);
        }
        if (executor instanceof org.bukkit.command.TabCompleter tabCompleter) {
            command.setTabCompleter(tabCompleter);
        }
    }

    private void saveResourceIfMissing(String name) {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().warning("Failed to create plugin data folder.");
        }
        java.io.File target = new java.io.File(getDataFolder(), name);
        if (!target.exists()) {
            saveResource(name, false);
        }
    }

    private void updateConfigs() {
        mergeYamlDefaults("config.yml");
        reloadConfig();
        mergeYamlDefaults("messages.yml");
        mergeYamlDefaults("menus.yml");
        migrateArenasFile();
        migrateKitsFile();
    }

    private void mergeYamlDefaults(String resourceName) {
        saveResourceIfMissing(resourceName);

        File file = new File(getDataFolder(), resourceName);
        YamlConfiguration current = YamlConfiguration.loadConfiguration(file);

        try (InputStream inputStream = getResource(resourceName)) {
            if (inputStream == null) {
                return;
            }

            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            mergeMissingKeys(current, defaults, "");
            saveYaml(file, current, resourceName);
        } catch (IOException exception) {
            getLogger().warning("Failed to merge defaults into " + resourceName + ": " + exception.getMessage());
        }
    }

    private void migrateArenasFile() {
        String resourceName = "arenas.yml";
        saveResourceIfMissing(resourceName);

        File file = new File(getDataFolder(), resourceName);
        YamlConfiguration current = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection arenas = current.getConfigurationSection("arenas");
        if (arenas == null) {
            current.createSection("arenas");
            saveYaml(file, current, resourceName);
            return;
        }

        for (String arenaId : arenas.getKeys(false)) {
            ConfigurationSection arena = arenas.getConfigurationSection(arenaId);
            if (arena == null) {
                continue;
            }
            if (!arena.contains("display-name")) {
                arena.set("display-name", arenaId);
            }
            ConfigurationSection spawn = arena.getConfigurationSection("spawn");
            if (spawn != null) {
                if (!spawn.contains("yaw")) {
                    spawn.set("yaw", 0.0D);
                }
                if (!spawn.contains("pitch")) {
                    spawn.set("pitch", 0.0D);
                }
            }
        }

        saveYaml(file, current, resourceName);
    }

    private void migrateKitsFile() {
        String resourceName = "kits.yml";
        saveResourceIfMissing(resourceName);

        File file = new File(getDataFolder(), resourceName);
        YamlConfiguration current = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection categories = current.getConfigurationSection("categories");
        if (categories == null) {
            categories = current.createSection("categories");
        }
        for (String categoryId : categories.getKeys(false)) {
            ConfigurationSection category = categories.getConfigurationSection(categoryId);
            if (category == null) {
                continue;
            }
            if (!category.contains("display-name")) {
                category.set("display-name", categoryId);
            }
            if (!category.contains("icon")) {
                category.set("icon", "CHEST");
            }
            if (!category.contains("slot")) {
                category.set("slot", 11);
            }
            if (!category.contains("lore")) {
                category.set("lore", java.util.List.of("&7Custom kit category."));
            }
        }

        ConfigurationSection kits = current.getConfigurationSection("kits");
        if (kits == null) {
            kits = current.createSection("kits");
        }
        for (String kitId : kits.getKeys(false)) {
            ConfigurationSection kit = kits.getConfigurationSection(kitId);
            if (kit == null) {
                continue;
            }
            if (!kit.contains("display-name")) {
                kit.set("display-name", kitId);
            }
            if (!kit.contains("category")) {
                kit.set("category", "");
            }
            if (!kit.contains("icon")) {
                kit.set("icon", "CHEST");
            }
            if (!kit.contains("slot")) {
                kit.set("slot", 11);
            }
            if (!kit.contains("arena")) {
                kit.set("arena", "");
            }
            if (!kit.contains("permission")) {
                kit.set("permission", "");
            }
            if (!kit.contains("lore")) {
                kit.set("lore", java.util.List.of("&7Custom FFA kit."));
            }
            if (!kit.contains("inventory-base64")) {
                kit.set("inventory-base64", "");
            }
            if (!kit.contains("armor-base64")) {
                kit.set("armor-base64", "");
            }
            if (!kit.contains("extra-base64")) {
                kit.set("extra-base64", "");
            }

            ConfigurationSection options = kit.getConfigurationSection("options");
            if (options == null) {
                options = kit.createSection("options");
            }
            if (!options.contains("regen-after-kill")) {
                options.set("regen-after-kill", false);
            }
            if (!options.contains("rekit-after-kill")) {
                options.set("rekit-after-kill", false);
            }
            if (!options.contains("combat-log-seconds")) {
                options.set("combat-log-seconds", getConfig().getInt("combat.tag-seconds", 15));
            }
            if (!options.contains("show-health-below-name")) {
                options.set("show-health-below-name", false);
            }
            if (!options.contains("drop-items-on-kill")) {
                options.set("drop-items-on-kill", false);
            }
        }

        saveYaml(file, current, resourceName);
    }

    private void saveYaml(File file, YamlConfiguration config, String resourceName) {
        try {
            config.save(file);
        } catch (IOException exception) {
            getLogger().warning("Failed to update " + resourceName + ": " + exception.getMessage());
        }
    }

    private void mergeMissingKeys(YamlConfiguration current, YamlConfiguration defaults, String basePath) {
        ConfigurationSection section = basePath.isEmpty() ? defaults : defaults.getConfigurationSection(basePath);
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            String path = basePath.isEmpty() ? key : basePath + "." + key;
            Object value = defaults.get(path);
            if (value instanceof ConfigurationSection) {
                if (!current.isConfigurationSection(path) && !current.contains(path)) {
                    current.createSection(path);
                }
                mergeMissingKeys(current, defaults, path);
                continue;
            }
            if (!current.contains(path)) {
                if (value instanceof List<?> list) {
                    current.set(path, list);
                } else {
                    current.set(path, value);
                }
            }
        }
    }
}
