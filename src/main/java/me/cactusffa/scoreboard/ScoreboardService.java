package me.cactusffa.scoreboard;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.KitDefinition;
import me.cactusffa.model.PlayerSession;
import me.cactusffa.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public final class ScoreboardService {

    private static final String OBJECTIVE = "cactusffa";
    private static final String BELOW_NAME_OBJECTIVE = "cactusffa_hp";

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
        long interval = Math.max(2L, plugin.getConfig().getLong("ffa.health-below-name.update-interval-ticks", 10L));
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!plugin.getConfig().getBoolean("ffa.scoreboard.enabled", true)) {
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.sessions().isInFfa(player)) {
                    apply(player);
                }
            }
        }, interval, interval);
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
        applyBelowName(scoreboard, player);

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
            objective.getScore(ColorUtil.legacy(rendered) + ChatColor.values()[Math.max(0, score - 1)]).setScore(score--);
        }
        player.setScoreboard(scoreboard);
    }

    public void clear(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private void applyBelowName(Scoreboard scoreboard, Player viewer) {
        KitDefinition kit = plugin.sessions().currentKit(viewer).orElse(null);
        if (kit == null || !kit.options().showHealthBelowName()) {
            return;
        }
        String rawRenderType = plugin.getConfig().getString("ffa.health-below-name.render-type", "INTEGER");
        RenderType renderType = "HEARTS".equalsIgnoreCase(rawRenderType) ? RenderType.HEARTS : RenderType.INTEGER;
        Component title = ColorUtil.component(resolveBelowNameLabel());
        Objective belowName = scoreboard.registerNewObjective(BELOW_NAME_OBJECTIVE, Criteria.DUMMY, title, renderType);
        belowName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!plugin.sessions().isInFfa(target)) {
                continue;
            }
            int health = (int) Math.ceil(Math.max(0.0D, target.getHealth()));
            belowName.getScore(target.getName()).setScore(health);
        }
    }

    private String resolveBelowNameLabel() {
        String format = plugin.getConfig().getString("ffa.health-below-name.format", "");
        if (format == null || format.isBlank()) {
            format = plugin.getConfig().getString("ffa.health-below-name.title", "&cHP");
        }
        String label = format
                .replace("%hearts%", "")
                .replace("%health%", "")
                .trim();
        return label.isBlank() ? "&cHP" : label;
    }
}
