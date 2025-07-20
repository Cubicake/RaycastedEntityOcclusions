package games.cubi.raycastedEntityOcclusion.Engine;

import games.cubi.raycastedEntityOcclusion.ConfigManager;
import games.cubi.raycastedEntityOcclusion.RaycastedEntityOcclusion;
import games.cubi.raycastedEntityOcclusion.Utils.*;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Engine {
    //TODO: Maybe store the data somewhere else? Could rework chunk storage to be general data storage
    public ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private AsyncScheduler asyncScheduler;
    private GlobalRegionScheduler globalScheduler;

    private final RaycastedEntityOcclusion plugin;
    private final ConfigManager config;

    public Engine(RaycastedEntityOcclusion plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        globalScheduler = plugin.getServer().getGlobalRegionScheduler();

        globalScheduler.runAtFixedRate(plugin, collectSyncData(), 1, 1);
    }

    public void registerPlayer(UUID playerUUID, boolean bypass) {
        playerDataMap.putIfAbsent(playerUUID, new PlayerData(playerUUID, bypass));
    }

    private Consumer<ScheduledTask> collectSyncData() {
        for (UUID playerUUID : playerDataMap.keySet()) {
            PlayerData data = playerDataMap.get(playerUUID);
            if (data.hasBypassPermission()) continue;

        }

        return task  -> {};
    }

    private Consumer<ScheduledTask> initiateSyncTasks() {

        for (UUID playerUUID : playerDataMap.keySet()) {
            PlayerData data = playerDataMap.get(playerUUID);

            if (data.hasBypassPermission()) continue;

            Map<UUID, Boolean> entities = data.getEntityVisibilityMap();
            Map<BlockLocation, Long> tileEntities = data.getSeenTileEntitiesMap();

            data.incrementTicksSinceVisibleEntityRecheck();
            if (data.getTicksSinceVisibleEntityRecheck() >= config.recheckInterval) {
                data.resetTicksSinceVisibleEntityRecheck();
            }
        }

        return task -> {};
    }
}
