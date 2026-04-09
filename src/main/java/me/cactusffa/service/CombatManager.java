package me.cactusffa.service;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.CombatTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class CombatManager {

    private final CactusFFAPlugin plugin;
    private final Map<UUID, CombatTag> tags = new HashMap<>();
    private int taskId = -1;
    private int tagSeconds;

    public CombatManager(CactusFFAPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        this.tagSeconds = plugin.getConfig().getInt("combat.tag-seconds", 15);
    }

    public void startTask() {
        shutdown();
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long now = System.currentTimeMillis();
            tags.entrySet().removeIf(entry -> entry.getValue().expiresAt() <= now);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!plugin.sessions().isInFfa(player)) {
                    continue;
                }
                Optional<CombatTag> tag = get(player.getUniqueId());
                if (tag.isPresent()) {
                    Player opponent = Bukkit.getPlayer(tag.get().opponent());
                    long seconds = Math.max(0L, (tag.get().expiresAt() - now + 999L) / 1000L);
                    String message = plugin.getConfig().getString("ffa.actionbar.combat", "&cIn combat with &f%opponent% &7(%seconds%s)")
                            .replace("%opponent%", opponent == null ? "Unknown" : opponent.getName())
                            .replace("%seconds%", String.valueOf(seconds));
                    player.sendActionBar(me.cactusffa.util.ColorUtil.component(message));
                } else {
                    String idle = plugin.getConfig().getString("ffa.actionbar.idle", "");
                    if (!idle.isBlank()) {
                        player.sendActionBar(me.cactusffa.util.ColorUtil.component(idle));
                    }
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

    public void tag(Player first, Player second) {
        if (!plugin.getConfig().getBoolean("combat.enabled", true)) {
            return;
        }
        int seconds = resolveCombatSeconds(first, second);
        long expires = System.currentTimeMillis() + (seconds * 1000L);
        upsert(first.getUniqueId(), second.getUniqueId(), expires);
        upsert(second.getUniqueId(), first.getUniqueId(), expires);
    }

    public boolean canDamage(Player attacker, Player victim) {
        if (!plugin.getConfig().getBoolean("combat.isolated-duel", true)) {
            return true;
        }
        CombatTag attackerTag = tags.get(attacker.getUniqueId());
        CombatTag victimTag = tags.get(victim.getUniqueId());
        if (attackerTag == null && victimTag == null) {
            return true;
        }
        if (attackerTag != null && !attackerTag.opponent().equals(victim.getUniqueId())) {
            return false;
        }
        if (victimTag != null && !victimTag.opponent().equals(attacker.getUniqueId())) {
            return false;
        }
        return true;
    }

    public Optional<CombatTag> get(UUID uniqueId) {
        CombatTag tag = tags.get(uniqueId);
        if (tag == null || tag.expiresAt() <= System.currentTimeMillis()) {
            tags.remove(uniqueId);
            return Optional.empty();
        }
        return Optional.of(tag);
    }

    public long remaining(UUID uniqueId) {
        return get(uniqueId).map(tag -> Math.max(0L, (tag.expiresAt() - System.currentTimeMillis() + 999L) / 1000L)).orElse(0L);
    }

    public void clear(UUID uniqueId) {
        CombatTag tag = tags.remove(uniqueId);
        if (tag != null) {
            CombatTag opponent = tags.get(tag.opponent());
            if (opponent != null && opponent.opponent().equals(uniqueId)) {
                tags.remove(tag.opponent());
            }
        }
    }

    public boolean isTagged(UUID uniqueId) {
        return get(uniqueId).isPresent();
    }

    private void upsert(UUID owner, UUID opponent, long expires) {
        CombatTag tag = tags.get(owner);
        if (tag == null) {
            tags.put(owner, new CombatTag(owner, opponent, expires));
            return;
        }
        tag.opponent(opponent);
        tag.expiresAt(expires);
    }

    private int resolveCombatSeconds(Player first, Player second) {
        int firstSeconds = plugin.sessions().currentKit(first)
                .map(kit -> kit.options().combatLogSeconds())
                .orElse(tagSeconds);
        int secondSeconds = plugin.sessions().currentKit(second)
                .map(kit -> kit.options().combatLogSeconds())
                .orElse(tagSeconds);
        return Math.max(1, Math.max(firstSeconds, secondSeconds));
    }
}
