package me.cactusffa.listener;

import me.cactusffa.CactusFFAPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.Map;

public final class CombatListener implements Listener {

    private final CactusFFAPlugin plugin;

    public CombatListener(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        Player attacker = resolveAttacker(event.getDamager());
        if (attacker == null) {
            return;
        }
        if (!plugin.sessions().isInFfa(attacker) || !plugin.sessions().isInFfa(victim)) {
            return;
        }
        if (!plugin.combat().canDamage(attacker, victim)) {
            plugin.combat().get(attacker.getUniqueId()).ifPresent(tag -> {
                Player opponent = Bukkit.getPlayer(tag.opponent());
                plugin.messages().send(attacker, "combat-target-only", Map.of("opponent", opponent == null ? "Unknown" : opponent.getName()));
            });
            event.setCancelled(true);
            return;
        }
        plugin.combat().tag(attacker, victim);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (!plugin.sessions().isInFfa(victim)) {
            return;
        }
        Player killer = victim.getKiller();
        if (killer != null && plugin.sessions().isInFfa(killer)) {
            plugin.sessions().recordKill(killer);
        }
        plugin.sessions().recordDeath(victim);
        plugin.combat().clear(victim.getUniqueId());
        if (plugin.getConfig().getBoolean("ffa.auto-respawn", true)) {
            Bukkit.getScheduler().runTask(plugin, () -> victim.spigot().respawn());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.sessions().isInFfa(event.getPlayer())) {
            return;
        }
        if (!plugin.combat().isTagged(event.getPlayer().getUniqueId())) {
            return;
        }
        String command = event.getMessage().startsWith("/") ? event.getMessage().substring(1) : event.getMessage();
        String base = command.split(" ")[0].toLowerCase();
        List<String> blocked = plugin.getConfig().getStringList("combat.block-commands");
        if (blocked.stream().anyMatch(value -> value.equalsIgnoreCase(base))) {
            plugin.messages().send(event.getPlayer(), "combat-blocked");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.combat().clear(event.getPlayer().getUniqueId());
        if (plugin.sessions().isInFfa(event.getPlayer())) {
            plugin.sessions().leave(event.getPlayer());
        }
    }

    private Player resolveAttacker(Entity entity) {
        if (entity instanceof Player player) {
            return player;
        }
        if (entity instanceof AbstractArrow arrow) {
            ProjectileSource shooter = arrow.getShooter();
            if (shooter instanceof Player player) {
                return player;
            }
        }
        return null;
    }
}
