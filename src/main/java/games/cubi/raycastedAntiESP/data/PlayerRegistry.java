package games.cubi.raycastedAntiESP.data;

import games.cubi.raycastedAntiESP.utils.PlayerData;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRegistry {

    private static PlayerRegistry instance;

    private PlayerRegistry() {}

    public static PlayerRegistry getInstance() {
        if (instance == null) {
            instance = new PlayerRegistry();
        }
        return instance;
    }

    private final ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public void registerPlayer(UUID playerUUID, boolean bypass) {
        playerDataMap.putIfAbsent(playerUUID, new PlayerData(playerUUID, bypass));
    }

    public PlayerData getPlayerData(UUID playerUUID) {
        return playerDataMap.get(playerUUID);
    }

    /**
     * @return Live, mutable collection of all PlayerData instances.
     * **/
    public Collection<PlayerData> getAllPlayerData() {
        return playerDataMap.values();
    }
}
