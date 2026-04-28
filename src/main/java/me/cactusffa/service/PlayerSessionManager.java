package me.cactusffa.service;

import me.cactusffa.CactusFFAPlugin;
import me.cactusffa.model.Arena;
import me.cactusffa.model.KitDefinition;
import me.cactusffa.model.PlayerSession;
import me.cactusffa.model.PlayerSnapshot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PlayerSessionManager {

    private final CactusFFAPlugin plugin;
    private final Map<UUID, PlayerSession> sessions = new HashMap<>();
    private final Map<UUID, String> lastKit = new HashMap<>();

    public PlayerSessionManager(CactusFFAPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isInFfa(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public Optional<PlayerSession> get(Player player) {
        return Optional.ofNullable(sessions.get(player.getUniqueId()));
    }

    public void join(Player player, KitDefinition kit, Arena arena) {
        PlayerSession session = sessions.computeIfAbsent(player.getUniqueId(), id -> new PlayerSession(id, snapshot(player)));
        session.currentArenaId(arena.id());
        session.currentKitId(kit.id());
        if (plugin.getConfig().getBoolean("ffa.save-last-kit", true)) {
            lastKit.put(player.getUniqueId(), kit.id());
        }

        player.closeInventory();
        player.teleport(arena.spawn());
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            applyKitState(player, kit);
            // Re-apply one tick later to beat late inventory mutations from practice/core plugins.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (isInFfa(player) && currentKit(player).map(current -> current.id().equalsIgnoreCase(kit.id())).orElse(false)) {
                    applyKitState(player, kit);
                }
            });
        });
    }

    public void rekit(Player player) {
        get(player).ifPresent(session -> plugin.kits().kit(session.currentKitId()).ifPresent(kit -> {
            applyKitState(player, kit);
        }));
    }

    public void leave(Player player) {
        PlayerSession session = sessions.remove(player.getUniqueId());
        plugin.combat().clear(player.getUniqueId());
        plugin.scoreboard().clear(player);
        if (session == null) {
            return;
        }
        restore(player, session.snapshot());
    }

    public void restoreAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (isInFfa(player)) {
                leave(player);
            }
        }
    }

    public void recordKill(Player killer) {
        get(killer).ifPresent(PlayerSession::addKill);
    }

    public void recordDeath(Player player) {
        if (!plugin.getConfig().getBoolean("stats.reset-kill-streak-on-death", true)) {
            return;
        }
        get(player).ifPresent(PlayerSession::resetKillStreak);
    }

    public String lastKit(UUID uniqueId) {
        return lastKit.get(uniqueId);
    }

    public Optional<KitDefinition> currentKit(Player player) {
        return get(player).flatMap(session -> plugin.kits().kit(session.currentKitId()));
    }

    private PlayerSnapshot snapshot(Player player) {
        Collection<PotionEffect> potionEffects = player.getActivePotionEffects();
        return new PlayerSnapshot(
                player.getLocation().clone(),
                copy(player.getInventory().getContents()),
                copy(player.getInventory().getArmorContents()),
                copy(player.getInventory().getExtraContents()),
                player.getHealth(),
                player.getFoodLevel(),
                player.getSaturation(),
                player.getLevel(),
                player.getExp(),
                player.getGameMode(),
                potionEffects
        );
    }

    private void restore(Player player, PlayerSnapshot snapshot) {
        player.getInventory().clear();
        player.getInventory().setContents(copy(snapshot.contents()));
        player.getInventory().setArmorContents(copy(snapshot.armor()));
        player.getInventory().setExtraContents(copy(snapshot.extra()));
        player.setGameMode(snapshot.gameMode());
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH) == null ? 20.0D : player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(snapshot.health(), maxHealth));
        player.setFoodLevel(snapshot.foodLevel());
        player.setSaturation(snapshot.saturation());
        player.setLevel(snapshot.level());
        player.setExp(snapshot.exp());
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : snapshot.potionEffects()) {
            player.addPotionEffect(effect);
        }
        Location destination = snapshot.location();
        if (plugin.getConfig().getBoolean("ffa.leave.use-main-lobby", true)) {
            Location lobby = plugin.worlds().mainLobbyLocation();
            if (lobby != null) {
                destination = lobby;
            }
        } else if (!plugin.getConfig().getBoolean("ffa.leave.teleport-to-join-location", true)) {
            Location fallback = plugin.worlds().fallbackLeaveLocation();
            if (fallback != null) {
                destination = fallback;
            }
        }
        if (destination != null) {
            player.teleport(destination);
        }
    }

    private ItemStack[] copy(ItemStack[] source) {
        if (source == null) {
            return new ItemStack[0];
        }
        ItemStack[] clone = new ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            clone[i] = source[i] == null ? null : source[i].clone();
        }
        return clone;
    }

    private void applyKitState(Player player, KitDefinition kit) {
        player.getInventory().clear();
        player.getInventory().setContents(copy(kit.contents()));
        player.getInventory().setArmorContents(copy(kit.armor()));
        player.getInventory().setExtraContents(copy(kit.extras()));
        player.updateInventory();
        player.setGameMode(GameMode.SURVIVAL);
        if (plugin.getConfig().getBoolean("ffa.clear-potion-effects-on-join", true)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }
        if (plugin.getConfig().getBoolean("ffa.heal-on-join", true)) {
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH) == null ? 20.0D : player.getAttribute(Attribute.MAX_HEALTH).getValue();
            player.setHealth(Math.min(maxHealth, 20.0D));
        }
        if (kit.options().hunger() && plugin.getConfig().getBoolean("ffa.feed-on-join", true)) {
            player.setFoodLevel(20);
            if (kit.options().saturation()) {
                player.setSaturation(20.0F);
            }
        }
        plugin.scoreboard().apply(player);
    }
}
