package games.cubi.raycastedAntiESP.data;

import java.util.HashMap;
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

    private final HashMap<UUID, Long> shouldShowEntity = new HashMap<>(); // Should only ever be accessed from main thread

    public void addEntityToShouldShowCache(UUID entityUUID) {
        synchronized (shouldShowEntity) {
            shouldShowEntity.put(entityUUID, System.currentTimeMillis());
        }
    }
    public boolean isEntityInShouldShowCache(UUID entityUUID) {
        synchronized (shouldShowEntity) {
            return shouldShowEntity.remove(entityUUID) != null;
        }
    }
    public void cleanShouldShowEntityCache() {
        long currentTime = System.currentTimeMillis();
        synchronized (shouldShowEntity) {
            shouldShowEntity.entrySet().removeIf(entry -> currentTime - entry.getValue() > 1000); // Remove entries older than 1 second
        }
    }
}
