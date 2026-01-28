package games.cubi.raycastedAntiESP.snapshot.entity;

import games.cubi.raycastedAntiESP.locatables.ThreadSafeLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.utils.EntityLocationPair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BukkitESM implements EntitySnapshotManager {

    /**UUID -> Entity UUID, QuantisedLocation -> Entity Location*/
    private final ConcurrentLinkedQueue<EntityLocationPair> entityLocProcessingQueue = new ConcurrentLinkedQueue<>();

    private final Set<UUID> playerUUIDs = ConcurrentHashMap.newKeySet();

    public BukkitESM() {
        Bukkit.getOnlinePlayers().forEach(player -> playerUUIDs.add(player.getUniqueId()));
    }

    @Override
    public void queueEntityLocationUpdate(UUID entityUUID, Location location) {
        entityLocProcessingQueue.add(new EntityLocationPair(entityUUID, location));
    }

    @Override
    public void removeEntityLocation(UUID entityUUID) {
        entityLocationMap.remove(entityUUID);
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

    @Override
    public ThreadSafeLocation getLocation(UUID entityUUID) {
        return entityLocationMap.get(entityUUID);
    }

    @Override
    public SnapshotManager.EntitySnapshotManagerType getType() {
        return SnapshotManager.EntitySnapshotManagerType.BUKKIT;
    }

    public HashMap<UUID, ThreadSafeLocation> getCopyOfEntityLocationMap() {
        return new HashMap<>(entityLocationMap);
    }

    public int getEntityCount() {
        return entityLocationMap.size();
    }

    public int getPlayerCount() {
        return playerUUIDs.size();
    }

    public void trackPlayer(UUID playerUUID) {
        playerUUIDs.add(playerUUID);
    }

    public void untrackPlayer(UUID playerUUID) {
        playerUUIDs.remove(playerUUID);
    }

}
