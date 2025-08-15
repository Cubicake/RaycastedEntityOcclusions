package games.cubi.raycastedEntityOcclusion.Utils;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder {

    private static final ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public static void registerPlayer(UUID playerUUID, boolean bypass) {
        playerDataMap.putIfAbsent(playerUUID, new PlayerData(playerUUID, bypass));
    }

    //Divider between methods for player data map and entity location map
    //
    //
    //Quantised locations are immutable, so there are no concerns about concurrent modification
    /**UUID -> Entity UUID, QuantisedLocation -> Entity Location*/
    private static volatile ConcurrentHashMap<UUID, QuantisedLocation> entityLocationMap = new ConcurrentHashMap<>();

    public static void updateEntireEntityLocationMap(HashMap<UUID, QuantisedLocation> newLocations) {
        entityLocationMap = new ConcurrentHashMap<>(newLocations);
    }

    public static QuantisedLocation getEntityLocation(UUID entityUUID) {
        return entityLocationMap.get(entityUUID);
    }

    public static HashMap<UUID, QuantisedLocation> getCopyOfEntityLocationMap() {
        return new HashMap<>(entityLocationMap);
    }

}
