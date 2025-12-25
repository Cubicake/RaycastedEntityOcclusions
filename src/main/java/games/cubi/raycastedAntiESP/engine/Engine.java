package games.cubi.raycastedAntiESP.engine;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.*;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.locatables.ThreadSafeLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.raycast.RaycastUtil;
import games.cubi.raycastedAntiESP.snapshot.block.BlockSnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.entity.BukkitESM;
import games.cubi.raycastedAntiESP.snapshot.entity.EntitySnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.locatables.Locatable;
import games.cubi.raycastedAntiESP.snapshot.tileentity.TileEntitySnapshotManager;
import games.cubi.raycastedAntiESP.utils.PlayerData;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.entity.EntityVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.player.PlayerVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity.TileEntityVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Engine {
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler bukkitScheduler;

    private final RaycastedAntiESP plugin;
    private final ConfigManager config;

    public Engine(RaycastedAntiESP plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        bukkitScheduler = plugin.getServer().getScheduler();

        forceEntityLocationUpdate();

        processEntityMovements(null); //first one will run on main thread but it shouldn't have to do much anyways
        flushLogCache(null);
    }

    //run async
    public void distributeTick() {
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
        EntitySnapshotManager entitySnapshotManager = SnapshotManager.getEntitySnapshotManager();
        TileEntitySnapshotManager tileEntitySnapshotManager = SnapshotManager.getTileEntitySnapshotManager();

        int maxRadius = Bukkit.getViewDistance() * 16;
        int tileEntityRadius = (Math.max(tileEntityConfig.getRaycastRadius(), maxRadius)+15)/16; //Fine to precompute this stuff cos a single division per tick is negligible

        for (List<PlayerData> batch : batches) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
                processTickForPlayers(batch, entityConfig, playerConfig, tileEntityConfig, tileEntityRadius, debugConfig.showDebugParticles(), blockSnapshotManager, entitySnapshotManager, tileEntitySnapshotManager);
                task.cancel();
            });
        }
    }

    private void processTickForPlayers(List<PlayerData> playerDataList, EntityConfig entityConfig, PlayerConfig playerConfig, TileEntityConfig tileEntityConfig, int tileEntityRadius, boolean debugParticles, BlockSnapshotManager blockSnapshotManager, EntitySnapshotManager entitySnapshotManager, TileEntitySnapshotManager tileEntitySnapshotManager) {
        for (PlayerData playerData : playerDataList) {
            if (playerData.hasBypassPermission()) continue;

            Locatable playerLocation = entitySnapshotManager.getLocation(playerData.getPlayerUUID());
            if (playerLocation == null) Logger.errorAndReturn(new RuntimeException("Player "+playerData.getPlayerUUID()+" does not have a location"), 3);

            if (entityConfig.isEnabled()) checkEntities(playerData, playerLocation, entityConfig, debugParticles, entitySnapshotManager, blockSnapshotManager);
            if (playerConfig.isEnabled()) checkPlayers(playerData, playerLocation, playerConfig, debugParticles, entitySnapshotManager, blockSnapshotManager);
            if (tileEntityConfig.isEnabled()) checkTileEntities(playerData, playerLocation, tileEntityConfig, tileEntityRadius, debugParticles, tileEntitySnapshotManager, blockSnapshotManager);
        }
    }
//TODO: Make sure player.getEntitiesNeedingRecheck is actually implemented
    private void checkEntities(PlayerData player, Locatable playerLocation, EntityConfig entityConfig, boolean debugParticles, EntitySnapshotManager entitySnapshotManager, BlockSnapshotManager blockSnapshotManager) {
        EntityVisibilityChanger entityVisibilityChanger = VisibilityChangeHandlers.getEntity();

        for (UUID entityUUID : player.getEntitiesNeedingRecheck(entityConfig.getVisibleRecheckInterval())) {
            Locatable entityLocation = entitySnapshotManager.getLocation(entityUUID);
            boolean canSee = RaycastUtil.raycast(playerLocation, entityLocation, entityConfig.getMaxOccludingCount(), entityConfig.getAlwaysShowRadius(), entityConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1 /*TODO stop hardcoding*/);
            entityVisibilityChanger.setEntityVisibilityForPlayer(player.getPlayerUUID(), entityUUID, canSee);
        }
    }

    private void checkPlayers(PlayerData player, Locatable playerLocation, PlayerConfig playerConfig, boolean debugParticles, EntitySnapshotManager entitySnapshotManager, BlockSnapshotManager blockSnapshotManager) {
        PlayerVisibilityChanger playerVisibilityChanger = VisibilityChangeHandlers.getPlayer();

        for (UUID otherPlayerUUID : player.getPlayersNeedingRecheck(playerConfig.getVisibleRecheckInterval())) {
            Locatable otherPlayerLocation = entitySnapshotManager.getLocation(otherPlayerUUID);
            boolean canSee = RaycastUtil.raycast(playerLocation, otherPlayerLocation, playerConfig.getMaxOccludingCount(), playerConfig.getAlwaysShowRadius(), playerConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1 /*TODO stop hardcoding*/);
            playerVisibilityChanger.setPlayerVisibilityForPlayer(player.getPlayerUUID(), otherPlayerUUID, canSee);
        }
    }

    private void checkTileEntities(PlayerData player, Locatable playerLocation, TileEntityConfig tileEntityConfig, int chunkRadius, boolean debugParticles, TileEntitySnapshotManager tileSnapshotManager, BlockSnapshotManager blockSnapshotManager) {
        //todo: How to get tile entities around player? Just chunkscan?
        TileEntityVisibilityChanger tileEntityVisibilityChanger = VisibilityChangeHandlers.getTileEntity();
        int chunkX = playerLocation.blockX() >> 4;
        int chunkZ = playerLocation.blockZ() >> 4;

        HashSet<BlockLocation> tileEntities = new HashSet<>();
        for (int x = chunkX-chunkRadius; x <= chunkRadius+chunkX; x++) {
            for (int z = chunkZ-chunkRadius; z <= chunkRadius+chunkZ; z++) {
                tileEntities.addAll(tileSnapshotManager.getTileEntitiesInChunk(playerLocation.world(), x, z));
            }
        }
        for (BlockLocation tileEntityLocation : tileEntities) {
            int timeSinceLastCheck = tileSnapshotManager.getTicksSincePlayerSawTileEntity(player.getPlayerUUID(), tileEntityLocation);
            if ((timeSinceLastCheck > -1) && (timeSinceLastCheck < tileEntityConfig.getVisibleRecheckInterval())) continue;
            boolean canSee = RaycastUtil.raycast(playerLocation, tileEntityLocation, tileEntityConfig.getMaxOccludingCount() + 1, tileEntityConfig.getAlwaysShowRadius(), tileEntityConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1 /*TODO stop hardcoding*/);
            tileEntityVisibilityChanger.setTileEntityVisibilityForPlayer(player.getPlayerUUID(), tileEntityLocation, canSee);
            if (canSee) {
                tileSnapshotManager.addOrUpdateTileEntityLastSeenMap(tileEntityLocation, player.getPlayerUUID(), DataHolder.getTick(), true);
            }
            else {
                tileSnapshotManager.removeFromTileEntityLastSeenMap(tileEntityLocation);
            }
        }
    }




    private void forceEntityLocationUpdate() {
        int recheckInterval = ConfigManager.get().getSnapshotConfig().getEntityLocationRefreshInterval();
        if (recheckInterval <= 0) {
            bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, 20 * 30); // Check again in 30 secs if config has changed
            return;
        }

        if (SnapshotManager.entitySnapshotManagerType() != SnapshotManager.EntitySnapshotManagerType.BUKKIT) {
            bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, recheckInterval);
            return;
        }

        HashMap<UUID, ThreadSafeLocation> entities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entities.put(entity.getUniqueId(), new ThreadSafeLocation(entity.getLocation(), entity.getHeight()));
            }
        }
        ((BukkitESM) SnapshotManager.getEntitySnapshotManager()).updateEntireEntityLocationMap(entities);
        bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, recheckInterval);
    }

    private void processEntityMovements(ScheduledTask scheduledTask) {

        if (SnapshotManager.entitySnapshotManagerType() != SnapshotManager.EntitySnapshotManagerType.BUKKIT) {
            asyncScheduler.runDelayed(plugin, this::processEntityMovements, 1, TimeUnit.SECONDS); // Check again in 1 second if config has changed
            return;
        }

        ((BukkitESM) SnapshotManager.getEntitySnapshotManager()).processEntityLocationQueue();
        asyncScheduler.runDelayed(plugin, this::processEntityMovements, 15, TimeUnit.MILLISECONDS);
    }

    private void flushLogCache(ScheduledTask scheduledTask) {
        if (config.getDebugConfig().logToFile()) {
            Logger.flush();
        }
        asyncScheduler.runDelayed(plugin, this::flushLogCache, 2, TimeUnit.SECONDS);
    }
}
