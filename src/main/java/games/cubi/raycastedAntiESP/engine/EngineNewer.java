package games.cubi.raycastedAntiESP.engine;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.*;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.locatables.ThreadSafeLocation;
import games.cubi.raycastedAntiESP.raycast.RaycastUtil;
import games.cubi.raycastedAntiESP.snapshot.BlockSnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.locatables.Locatable;
import games.cubi.raycastedAntiESP.utils.PlayerData;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EngineNewer {
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler bukkitScheduler;

    private final RaycastedAntiESP plugin;
    private final ConfigManager config;

    public EngineNewer(RaycastedAntiESP plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        bukkitScheduler = plugin.getServer().getScheduler();

        forceEntityLocationUpdate();
        processEntityMovements(null); //first one will run on main thread but it shouldn't have to do much anyways
        clearOldCacheEntries();
        flushLogCache(null);
    }

    //run async
    private void distributeTick() {
        Collection<PlayerData> allPlayers = DataHolder.players().getAllPlayerData();
        int threads = 1; //TODO Don't hardcode
        if (threads < 1) threads = 1;

        List<List<PlayerData>> batches = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            batches.add(new ArrayList<>());
        }

        int index = 0;
        for (PlayerData playerData : allPlayers) {
            batches.get(index++ % threads).add(playerData);
        }

        EntityConfig entityConfig = config.getEntityConfig();
        PlayerConfig playerConfig = config.getPlayerConfig();
        TileEntityConfig tileEntityConfig = config.getTileEntityConfig();
        DebugConfig debugConfig = config.getDebugConfig();
        BlockSnapshotManager blockSnapshotManager = SnapshotManager.getBlockSnapshotManager();

        int maxRadius = Bukkit.getViewDistance() * 16;
        int tileEntityRadius = (Math.max(tileEntityConfig.getRaycastRadius(), maxRadius)+15)/16; //Fine to precompute this stuff cos a single division per tick is negligible

        for (List<PlayerData> batch : batches) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
                processTickForPlayers(batch, entityConfig, playerConfig, tileEntityConfig, tileEntityRadius, debugConfig, blockSnapshotManager);
                task.cancel();
            });
        }
    }

    private void processTickForPlayers(List<PlayerData> playerDataList, EntityConfig entityConfig, PlayerConfig playerConfig, TileEntityConfig tileEntityConfig, int tileEntityRadius, DebugConfig debugConfig, BlockSnapshotManager blockSnapshotManager) {
        for (PlayerData playerData : playerDataList) {
            if (playerData.hasBypassPermission()) continue;

            Locatable playerLocation = SnapshotManager.getEntitySnapshotManager().getLocation(playerData.getPlayerUUID());
            if (playerLocation == null) Logger.errorAndReturn(new RuntimeException("Player "+playerData.getPlayerUUID()+" does not have a location"));

            if (entityConfig.isEnabled()) checkEntities(playerData, playerLocation, entityConfig, debugConfig, blockSnapshotManager);
            if (playerConfig.isEnabled()) checkPlayers(playerData, playerLocation, playerConfig, debugConfig, blockSnapshotManager);
            if (tileEntityConfig.isEnabled()) checkTileEntities(playerData, playerLocation, tileEntityConfig, tileEntityRadius, debugConfig, blockSnapshotManager);
        }
    }
//TODO: Make sure player.getEntitiesNeedingRecheck is actually implemented
    private void checkEntities(PlayerData player, Locatable playerLocation, EntityConfig entityConfig, DebugConfig debugConfig, BlockSnapshotManager blockSnapshotManager) {
        for (UUID entityUUID : player.getEntitiesNeedingRecheck(entityConfig.getVisibleRecheckInterval())) {
            Locatable entityLocation = SnapshotManager.getEntitySnapshotManager().getLocation(entityUUID);
            boolean canSee = RaycastUtil.raycast(playerLocation, entityLocation, entityConfig.getMaxOccludingCount(), entityConfig.getAlwaysShowRadius(), entityConfig.getRaycastRadius(), debugConfig.showDebugParticles(), blockSnapshotManager, 1 /*TODO stop hardcoding*/);
            VisibilityChangeHandlers.getEntity().setEntityVisibilityForPlayer(player.getPlayerUUID(), entityUUID, canSee);
        }
    }

    private void checkPlayers(PlayerData player, Locatable playerLocation, PlayerConfig playerConfig, DebugConfig debugConfig, BlockSnapshotManager blockSnapshotManager) {
        for (UUID otherPlayerUUID : player.getPlayersNeedingRecheck(playerConfig.getVisibleRecheckInterval())) {
            Locatable otherPlayerLocation = SnapshotManager.getEntitySnapshotManager().getLocation(otherPlayerUUID);
            boolean canSee = RaycastUtil.raycast(playerLocation, otherPlayerLocation, playerConfig.getMaxOccludingCount(), playerConfig.getAlwaysShowRadius(), playerConfig.getRaycastRadius(), debugConfig.showDebugParticles(), blockSnapshotManager, 1 /*TODO stop hardcoding*/);
        }
    }

    private void checkTileEntities(PlayerData player, Locatable playerLocation, TileEntityConfig tileEntityConfig, int radius, DebugConfig debugConfig, BlockSnapshotManager blockSnapshotManager) {
        //todo: How to get tile entities around player? Just chunkscan?
    }




    private void forceEntityLocationUpdate() {
        int recheckInterval = ConfigManager.get().getSnapshotConfig().getEntityLocationRefreshInterval();
        if (recheckInterval <= 0) {
            bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, 20 * 30); // Check again in 30 secs if config has changed
            return;
        }
        HashMap<UUID, ThreadSafeLocation> entities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entities.put(entity.getUniqueId(), new ThreadSafeLocation(entity.getLocation(), entity.getHeight()));
            }
        }
        DataHolder.entityLocation().updateEntireEntityLocationMap(entities);
        bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, recheckInterval);
    }

    private void processEntityMovements(ScheduledTask scheduledTask) {
        DataHolder.entityLocation().processEntityLocationQueue();
        asyncScheduler.runDelayed(plugin, this::processEntityMovements, 15, TimeUnit.MILLISECONDS);
    }

    private void clearOldCacheEntries() {
        DataHolder.entityVisibility().cleanShouldShowEntityCache();
        bukkitScheduler.runTaskLater(plugin, this::clearOldCacheEntries, 20 * 120);
    }

    private void flushLogCache(ScheduledTask scheduledTask) {
        if (config.getDebugConfig().logToFile()) {
            Logger.flush();
        }
        asyncScheduler.runDelayed(plugin, this::flushLogCache, 2, TimeUnit.SECONDS);
    }
}
