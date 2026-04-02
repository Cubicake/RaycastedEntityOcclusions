package games.cubi.raycastedantiesp.paper.snapshot.entity;

import games.cubi.locatables.implementations.ThreadSafeLocatable;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.entity.EntitySnapshotManager;
import games.cubi.raycastedantiesp.paper.utils.EntityLocationPair;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BukkitESM implements EntitySnapshotManager {

    /**UUID -> Entity UUID, QuantisedLocation -> Entity Location*/
    private final ConcurrentLinkedQueue<EntityLocationPair> entityLocProcessingQueue = new ConcurrentLinkedQueue<>();

    public BukkitESM() {
    }

    public void queueEntityLocationUpdate(UUID entityUUID, Location location) {
        entityLocProcessingQueue.add(new EntityLocationPair(entityUUID, location));
    }

    @Override
    public void removeEntityLocation(UUID entityUUID) {
        entityLocationMap.remove(entityUUID);
    }

    private volatile ConcurrentHashMap<UUID, ThreadSafeLocatable> entityLocationMap = new ConcurrentHashMap<>();

    public void updateEntireEntityLocationMap(HashMap<UUID, ThreadSafeLocatable> newLocations) {
        entityLocationMap = new ConcurrentHashMap<>(newLocations);
    }

    private void setOrUpdateEntityLocation(UUID entityUUID, Location location) {
        entityLocationMap.compute(entityUUID, (uuid, oldLoc) -> {
            if (oldLoc == null) return new ThreadSafeLocatable(location.getWorld().getUID(), location.x(), location.y(), location.z());
            oldLoc.set(location.x(), location.y(), location.z(), location.getWorld().getUID()); // Not atomic, but good enough hopefully.
            return oldLoc;
        });
    }

    // Run this async
    public void processEntityLocationQueue() {
        while (!entityLocProcessingQueue.isEmpty()) {
            EntityLocationPair entityLocationPair = entityLocProcessingQueue.poll();
            if (entityLocationPair == null) return;
            setOrUpdateEntityLocation(entityLocationPair.entity(), entityLocationPair.loc());
        }
    }

    @Override
    public ThreadSafeLocatable getLocation(UUID entityUUID) {
        return entityLocationMap.get(entityUUID);
    }

    @Override
    public SnapshotManager.EntitySnapshotManagerType getType() {
        return SnapshotManager.EntitySnapshotManagerType.BUKKIT;
    }

    public HashMap<UUID, ThreadSafeLocatable> getCopyOfEntityLocationMap() {
        return new HashMap<>(entityLocationMap);
    }

}
