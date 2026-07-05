package dev.cuteecactus.profile;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileManager {
    private static ProfileManager instance;

    private final ConcurrentHashMap<UUID, Profile> profiles = new ConcurrentHashMap<>();

    public ProfileManager () {
        instance = this;
    }

    public static ProfileManager get () {
        return instance;
    }

    public void addProfile (UUID uuid) {
        profiles.put(uuid, new Profile(uuid));
    }

    public void removeProfile (UUID uuid) {
        profiles.remove(uuid);
    }

    public Profile getProfile (UUID uuid) {
        return profiles.getOrDefault(uuid, null);
    }
}
