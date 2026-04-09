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
import org.bukkit.plugin.java.JavaPlugin;

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
        saveDefaultConfig();
        saveResourceIfMissing("messages.yml");
        saveResourceIfMissing("menus.yml");
        saveResourceIfMissing("arenas.yml");
        saveResourceIfMissing("kits.yml");

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
        reloadConfig();
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
        register("cactusffa", new AdminCommand(this));
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
}
