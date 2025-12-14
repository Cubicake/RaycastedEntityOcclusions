package games.cubi.raycastedAntiESP.data;

import games.cubi.raycastedAntiESP.locatables.ThreadSafeLocation;
import games.cubi.raycastedAntiESP.utils.EntityLocationPair;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EntityLocationService { //todo is this an impl of entitysnapshotmanager?
    private static EntityLocationService instance;

    private EntityLocationService() {}

    public static EntityLocationService getInstance() {
        if (instance == null) {
            instance = new EntityLocationService();
        }
        return instance;
    }

    /**UUID -> Entity UUID, QuantisedLocation -> Entity Location*/
    private final ConcurrentLinkedQueue<EntityLocationPair> entityLocProcessingQueue = new ConcurrentLinkedQueue<>();

    public void queueEntityLocationUpdate(UUID entityUUID, Location location) {
        entityLocProcessingQueue.add(new EntityLocationPair(entityUUID, location));
    }

    private volatile ConcurrentHashMap<UUID, ThreadSafeLocation> entityLocationMap = new ConcurrentHashMap<>();

    public void updateEntireEntityLocationMap(HashMap<UUID, ThreadSafeLocation> newLocations) {
        entityLocationMap = new ConcurrentHashMap<>(newLocations);
    }

    private void setOrUpdateEntityLocation(UUID entityUUID, Vector location, UUID world) {
        entityLocationMap.compute(entityUUID, (uuid, oldLoc) -> {
            if (oldLoc == null) return new ThreadSafeLocation(location, world);
            while (!oldLoc.update(location)) {
                Thread.onSpinWait(); //doesn't actually sleep the thread, just indicates that this thread should be deprioritized. Once the write lock is released it should write nearly immediately
            }
            return oldLoc;
        });
    }

    private void setOrUpdateEntityLocation(UUID entityUUID, Location location) {
        setOrUpdateEntityLocation(entityUUID, location.toVector(), location.getWorld().getUID());
    }

    // Run this async
    public void processEntityLocationQueue() {
        while (!entityLocProcessingQueue.isEmpty()) {
            EntityLocationPair entityLocationPair = entityLocProcessingQueue.poll();
            if (entityLocationPair == null) return;
            setOrUpdateEntityLocation(entityLocationPair.entity(), entityLocationPair.loc());
        }
    }

    public ThreadSafeLocation getEntityLocation(UUID entityUUID) {
        return entityLocationMap.get(entityUUID);
    }

    public HashMap<UUID, ThreadSafeLocation> getCopyOfEntityLocationMap() {
        return new HashMap<>(entityLocationMap);
    }

}
