package games.cubi.raycastedAntiESP.deletioncandidates;

import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
@Deprecated(forRemoval = true)
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

    private record ShowOrHideEntityToPlayer (UUID entityUUID, UUID playerUUID, boolean show) {}

    private final ConcurrentLinkedQueue<ShowOrHideEntityToPlayer> entityVisibilityProcessingQueue = new ConcurrentLinkedQueue<>();

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

    public void showEntityAsync(UUID entityUUID, UUID playerUUID) {
        addToVisibilityChangeCache(entityUUID, playerUUID);
        addToEntityVisibilityProcessingQueue(entityUUID, playerUUID, true);
    }

    public void hideEntityAsync(UUID entityUUID, UUID playerUUID) {
        addToVisibilityChangeCache(entityUUID, playerUUID);
        addToEntityVisibilityProcessingQueue(entityUUID, playerUUID, false);
    }

    private void addToEntityVisibilityProcessingQueue(UUID entityUUID, UUID playerUUID, boolean show) {
        entityVisibilityProcessingQueue.add(new ShowOrHideEntityToPlayer(entityUUID, playerUUID, show));
    }

    private void processEntityVisibilityProcessingQueue() {
        while (!entityVisibilityProcessingQueue.isEmpty()) {
            ShowOrHideEntityToPlayer task = entityVisibilityProcessingQueue.poll();
            if (task == null) return;
            Entity entity = Bukkit.getEntity(task.entityUUID());
            Player player = Bukkit.getPlayer(task.playerUUID());
            if (entity == null || player == null) continue; // Entity or player might have logged out or despawned
            if (task.show()) {
                player.showEntity(RaycastedAntiESP.get(), entity);
            } else {
                player.hideEntity(RaycastedAntiESP.get(), entity);
            }
        }
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
