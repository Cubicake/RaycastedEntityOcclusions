package games.cubi.raycastedantiesp.core.players;

import games.cubi.locatables.BlockLocatable;

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

    public void registerPlayer(UUID playerUUID, boolean hasBypassPermission, int joinTick) {
        playerDataMap.put(playerUUID, new PlayerData(playerUUID, hasBypassPermission, joinTick));
    }

    public PlayerData registerAndGetPlayer(UUID playerUUID, boolean hasBypassPermission, int joinTick) {
        PlayerData newData = new PlayerData(playerUUID, hasBypassPermission, joinTick);
        PlayerData existingData = playerDataMap.putIfAbsent(playerUUID, newData);
        return existingData != null ? existingData : newData;
    }

    public void unregisterPlayer(UUID playerUUID) {
        playerDataMap.remove(playerUUID);
    }

    public PlayerData getPlayerData(UUID playerUUID) {
        return playerDataMap.get(playerUUID);
    }

    public boolean isPlayerRegistered(UUID playerUUID) {
        return playerDataMap.containsKey(playerUUID);
    }

    /**
     * @return Live, mutable collection of all PlayerData instances.
     * **/
    public Collection<PlayerData> getAllPlayerData() {
        return playerDataMap.values();
    }

    /**For use when an invalid tile entity is detected, to ensure all players have it removed from their data**/
    public void removeTileEntityFromAllPlayers(BlockLocatable blockLocation) {
        for (PlayerData playerData : playerDataMap.values()) {
            playerData.tileVisibility().remove(blockLocation);
        }
    }
}
