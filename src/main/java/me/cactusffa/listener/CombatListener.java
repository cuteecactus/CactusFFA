package me.cactusffa.listener;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.KitDefinition;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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
        KitDefinition victimKit = plugin.sessions().currentKit(victim).orElse(null);
        Player killer = victim.getKiller();
        if (killer != null && plugin.sessions().isInFfa(killer)) {
            plugin.sessions().recordKill(killer);
            applyKillRewards(killer);
        }
        handleDrops(event, victim, victimKit);
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

    private void applyKillRewards(Player killer) {
        plugin.sessions().currentKit(killer).ifPresent(kit -> {
            if (kit.options().regenAfterKill()) {
                double maxHealth = killer.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH) == null
                        ? 20.0D
                        : killer.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                killer.setHealth(Math.min(maxHealth, killer.getHealth() + 8.0D));
                killer.setFoodLevel(20);
                killer.setSaturation(20.0F);
            }
            if (kit.options().rekitAfterKill()) {
                Bukkit.getScheduler().runTask(plugin, () -> plugin.sessions().rekit(killer));
            }
        });
    }

    private void handleDrops(PlayerDeathEvent event, Player victim, KitDefinition victimKit) {
        event.setKeepInventory(true);
        event.getDrops().clear();
        if (victimKit == null || !victimKit.options().dropItemsOnKill()) {
            return;
        }
        org.bukkit.inventory.ItemStack[] contents = copy(victim.getInventory().getContents());
        org.bukkit.inventory.ItemStack[] armor = copy(victim.getInventory().getArmorContents());
        org.bukkit.inventory.ItemStack[] extra = copy(victim.getInventory().getExtraContents());
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (plugin.getConfig().getBoolean("ffa.kill-drops.include-main-inventory", true)) {
                dropInventory(victim, contents);
            }
            if (plugin.getConfig().getBoolean("ffa.kill-drops.include-armor", true)) {
                dropInventory(victim, armor);
            }
            if (plugin.getConfig().getBoolean("ffa.kill-drops.include-extra", true)) {
                dropInventory(victim, extra);
            }
        });
    }

    private void dropInventory(Player player, org.bukkit.inventory.ItemStack[] items) {
        for (org.bukkit.inventory.ItemStack item : items) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            Item dropped = player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
            dropped.setOwner(null);
        }
    }

    private org.bukkit.inventory.ItemStack[] copy(org.bukkit.inventory.ItemStack[] source) {
        if (source == null) {
            return new org.bukkit.inventory.ItemStack[0];
        }
        org.bukkit.inventory.ItemStack[] clone = new org.bukkit.inventory.ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            clone[i] = source[i] == null ? null : source[i].clone();
        }
        return clone;
    }
}
