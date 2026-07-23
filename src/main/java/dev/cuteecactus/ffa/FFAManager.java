package dev.cuteecactus.ffa;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import dev.cuteecactus.arena.Arena;
import dev.cuteecactus.config.MessageConfig;
import dev.cuteecactus.kits.Kit;
import dev.cuteecactus.kits.KitManager;
import dev.cuteecactus.lobby.LobbyManager;
import dev.cuteecactus.profile.Profile;
import dev.cuteecactus.profile.ProfileManager;
import dev.cuteecactus.profile.ProfileState;

public class FFAManager {
    private static FFAManager instance;

    private ConcurrentHashMap<String, Set<UUID>> ffas = new ConcurrentHashMap<>();

    public FFAManager() {
        instance = this;
    }

    public static FFAManager get() {
        return instance;
    }

    public void joinFFA(Player player, Kit kit) {
        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null) return;

        Arena arena = kit.getArena();
        if (arena == null) {
            player.sendMessage(MessageConfig.get().getMessage("errors.no-arena"));
            return;
        }

        player.teleport(arena.getSpawn());
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        player.getInventory().setContents(kit.getContent());

        profile.setCurrentArena(arena);
        profile.setCurrentKit(kit);
        profile.setProfileState(ProfileState.IN_FFA);

        ffas.computeIfAbsent(kit.getId().toLowerCase(), k -> ConcurrentHashMap.newKeySet())
                .add(player.getUniqueId());
    }

    public void leaveFFA(Player player) {
        Profile profile = ProfileManager.get().getProfile(player.getUniqueId());
        if (profile == null) return;

        if (profile.getCurrentKit() != null) {
            removePlayer(profile.getCurrentKit().getId(), player.getUniqueId());
        }

        profile.setCurrentKit(null);
        profile.setCurrentArena(null);

        LobbyManager.get().load(player);
    }

    private void removePlayer(String kit, UUID player) {
        Set<UUID> players = ffas.get(kit);

        if (players == null) {
            return;
        }

        players.remove(player);

        if (players.isEmpty()) {
            ffas.remove(kit, players);
        }
    }

    public void removePlayerFromAll(UUID player) {
        for (Entry<String, Set<UUID>> entry : ffas.entrySet()) {
            if (entry.getValue().remove(player)) {
                if (entry.getValue().isEmpty()) {
                    ffas.remove(entry.getKey(), entry.getValue());
                }
                return;
            }
        }
    }

    public Set<UUID> getPlayers(String kit) {
        return ffas.getOrDefault(kit, Collections.emptySet());
    }

    public int getPlayerCount(String kit) {
        return getPlayers(kit).size();
    }

    public boolean containsPlayer(String kit, UUID player) {
        return getPlayers(kit).contains(player);
    }

    public boolean exists(String kit) {
        return ffas.containsKey(kit);
    }

    public void removeFFA(String kit) {
        ffas.remove(kit);
    }

    public void clear() {
        ffas.clear();
    }

    public Set<String> getFFAs() {
        return ffas.keySet();
    }
}
