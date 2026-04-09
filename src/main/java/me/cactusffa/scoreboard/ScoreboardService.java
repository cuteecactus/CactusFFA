package me.cactusffa.scoreboard;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.PlayerSession;
import me.cactusffa.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public final class ScoreboardService {

    private static final String OBJECTIVE = "cactusffa";

    private final CactusFFAPlugin plugin;
    private int taskId = -1;

    public ScoreboardService(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.sessions().isInFfa(player)) {
                apply(player);
            }
        }
    }

    public void startTask() {
        shutdown();
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!plugin.getConfig().getBoolean("ffa.scoreboard.enabled", true)) {
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.sessions().isInFfa(player)) {
                    apply(player);
                }
            }
        }, 20L, 20L);
    }

    public void shutdown() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public void apply(Player player) {
        if (!plugin.getConfig().getBoolean("ffa.scoreboard.enabled", true)) {
            return;
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Component title = ColorUtil.component(plugin.getConfig().getString("ffa.scoreboard.title", "&a&lCactusFFA"));
        Objective objective = scoreboard.registerNewObjective(OBJECTIVE, Criteria.DUMMY, title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        PlayerSession session = plugin.sessions().get(player).orElse(null);
        List<String> lines = plugin.getConfig().getStringList("ffa.scoreboard.lines");
        int score = lines.size();
        for (String line : lines) {
            String rendered = line
                    .replace("%kit%", session == null ? "-" : session.currentKitId())
                    .replace("%arena%", session == null ? "-" : session.currentArenaId())
                    .replace("%kills%", session == null ? "0" : String.valueOf(session.kills()))
                    .replace("%streak%", session == null ? "0" : String.valueOf(session.killStreak()))
                    .replace("%combat%", String.valueOf(plugin.combat().remaining(player.getUniqueId())));
            objective.getScore(ColorUtil.text(rendered) + " ".repeat(Math.max(0, lines.size() - score))).setScore(score--);
        }
        player.setScoreboard(scoreboard);
    }

    public void clear(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
