package games.cubi.raycastedAntiESP.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataHolder {

    public static boolean packetEventsPresent;

    private static final ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public static void registerPlayer(UUID playerUUID, boolean bypass) {
        playerDataMap.putIfAbsent(playerUUID, new PlayerData(playerUUID, bypass));
    }

    //Divider between methods for player data map and entity location map
    //
    //
    //Quantised locations are immutable, so there are no concerns about concurrent modification
    /**UUID -> Entity UUID, QuantisedLocation -> Entity Location*/
    private static final ConcurrentLinkedQueue<EntityLocationPair> entityLocProcessingQueue = new ConcurrentLinkedQueue<>();

    public static void queueEntityLocationUpdate(UUID entityUUID, Location location) {
        entityLocProcessingQueue.add(new EntityLocationPair(entityUUID, location));
    }

    private static volatile ConcurrentHashMap<UUID, ThreadSafeLoc> entityLocationMap = new ConcurrentHashMap<>();

    public static void updateEntireEntityLocationMap(HashMap<UUID, ThreadSafeLoc> newLocations) {
        entityLocationMap = new ConcurrentHashMap<>(newLocations);
    }

    private static void setOrUpdateEntityLocation(UUID entityUUID, Vector location, UUID world) {
        entityLocationMap.compute(entityUUID, (uuid, oldLoc) -> {
            if (oldLoc == null) return new ThreadSafeLoc(location, world);
            while (!oldLoc.update(location)) {
                Thread.onSpinWait(); //doesn't actually sleep the thread, just indicates that this thread should be deprioritized. Once the write lock is released it should write nearly immediately
            }
            return oldLoc;
        });
    }

    private static void setOrUpdateEntityLocation(UUID entityUUID, Location location) {
        setOrUpdateEntityLocation(entityUUID, location.toVector(), location.getWorld().getUID());
    }

    // Run this async
    public static void processEntityLocationQueue() {
        while (!entityLocProcessingQueue.isEmpty()) {
            EntityLocationPair entityLocationPair = entityLocProcessingQueue.poll();
            if (entityLocationPair == null) return;
            setOrUpdateEntityLocation(entityLocationPair.getEntity(), entityLocationPair.getLoc());
        }
    }

    public static ThreadSafeLoc getEntityLocation(UUID entityUUID) {
        return entityLocationMap.get(entityUUID);
    }

    public static HashMap<UUID, ThreadSafeLoc> getCopyOfEntityLocationMap() {
        return new HashMap<>(entityLocationMap);
    }

}
