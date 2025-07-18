package games.cubi.raycastedEntityOcclusion.Engine;

import games.cubi.raycastedEntityOcclusion.ConfigManager;
import games.cubi.raycastedEntityOcclusion.RaycastedEntityOcclusion;
import games.cubi.raycastedEntityOcclusion.Utils.*;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Engine {
    //TODO: Maybe store the data somewhere else? Could rework chunk storage to be general data storage
    ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    private final RaycastedEntityOcclusion plugin;
    private final ConfigManager config;

    public Engine(RaycastedEntityOcclusion plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
    }

    public void registerPlayer(UUID playerUUID) {
        playerDataMap.putIfAbsent(playerUUID, new PlayerData(playerUUID));
    }


}
