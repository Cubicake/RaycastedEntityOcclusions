package games.cubi.raycastedAntiESP.data;

import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EntityVisibilityChangeCache {
    private static EntityVisibilityChangeCache instance;

    private EntityVisibilityChangeCache() {}

    public static EntityVisibilityChangeCache getInstance() {
        if (instance == null) {
            instance = new EntityVisibilityChangeCache();
        }
        return instance;
    }

    private record PlayerEntityPair(UUID playerUUID, UUID entityUUID) {}

    private final HashMap<PlayerEntityPair, Long> visibilityStatusChange = new HashMap<>(); // Should only ever be accessed from main thread

    public void showEntity(UUID entityUUID, Player player) {
        Entity entity = Bukkit.getEntity(entityUUID);
        Objects.requireNonNull(entity);
        showEntity(entity, entityUUID, player);
    }
    public void showEntity(Entity entity, Player player) {
        showEntity(entity, entity.getUniqueId(), player);
    }

    public void showEntity(Entity entity, UUID entityUUID, Player player) {
        addToVisibilityChangeCache(entityUUID, player.getUniqueId());
        player.showEntity(RaycastedAntiESP.get(), entity);
    }

    public void hideEntity(UUID entityUUID, Player player) {
        Entity entity = Bukkit.getEntity(entityUUID);
        Objects.requireNonNull(entity);
        hideEntity(entity, entityUUID, player);
    }

    public void hideEntity(Entity entity, Player player) {
        hideEntity(entity, entity.getUniqueId(), player);
    }

    public void hideEntity(Entity entity, UUID entityUUID, Player player) {
        addToVisibilityChangeCache(entityUUID, player.getUniqueId());
        player.hideEntity(RaycastedAntiESP.get(), entity);
    }

    private void addToVisibilityChangeCache(UUID entityUUID, UUID playerUUID) {
        synchronized (visibilityStatusChange) {
            visibilityStatusChange.put(new PlayerEntityPair(playerUUID, entityUUID), System.currentTimeMillis());
        }
    }
    public boolean entityVisibilityShouldChange(UUID entityUUID, UUID playerUUID) {
        synchronized (visibilityStatusChange) {
            return visibilityStatusChange.remove(new PlayerEntityPair(playerUUID, entityUUID)) != null;
        }
    }

    public void cleanShouldShowEntityCache() {
        long currentTime = System.currentTimeMillis();
        synchronized (visibilityStatusChange) {
            visibilityStatusChange.entrySet().removeIf(entry -> currentTime - entry.getValue() > 1000); // Remove entries older than 1 second
        }
    }
}
