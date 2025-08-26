package games.cubi.raycastedAntiESP.utils;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    private static volatile ConcurrentHashMap<UUID, ThreadSafeLoc> entityLocationMap = new ConcurrentHashMap<>();

    public static void updateEntireEntityLocationMap(HashMap<UUID, ThreadSafeLoc> newLocations) {
        entityLocationMap = new ConcurrentHashMap<>(newLocations);
    }

    public static ThreadSafeLoc getEntityLocation(UUID entityUUID) {
        return entityLocationMap.get(entityUUID);
    }

    public static HashMap<UUID, ThreadSafeLoc> getCopyOfEntityLocationMap() {
        return new HashMap<>(entityLocationMap);
    }

}
