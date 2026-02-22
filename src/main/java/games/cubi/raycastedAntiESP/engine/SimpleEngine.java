package games.cubi.raycastedAntiESP.engine;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.*;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.locatables.ThreadSafeLocation;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.raycast.RaycastUtil;
import games.cubi.raycastedAntiESP.snapshot.block.BlockSnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.block.BukkitBSM;
import games.cubi.raycastedAntiESP.snapshot.entity.BukkitESM;
import games.cubi.raycastedAntiESP.snapshot.entity.EntitySnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.locatables.Locatable;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class SimpleEngine implements Engine {
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler bukkitScheduler;

    private final RaycastedAntiESP plugin;
    private final ConfigManager config;

    public SimpleEngine(RaycastedAntiESP plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        bukkitScheduler = plugin.getServer().getScheduler();

        forceEntityLocationUpdate();

        processEntityMovements(null); //first one will run on main thread but it shouldn't have to do much anyways
        flushLogCache(null);
    }

    public final ConcurrentLinkedQueue<AbstractBlockLocation> recheckQueue = new ConcurrentLinkedQueue<>();

    public void syncRecheck() {
        while (!recheckQueue.isEmpty()) {
            AbstractBlockLocation location = recheckQueue.poll();

            World world = Bukkit.getWorld(location.world());
            if (world == null) continue;

            ((BukkitBSM) SnapshotManager.getBlockSnapshotManager()).snapshotChunk(world.getChunkAt(location.blockX() >> 4, location.blockZ() >> 4));
        }
    }

    @Override
    public void tick() {
        distributeTick();
    }

    //run async
    private void distributeTick() {
        final int currentTick = DataHolder.getTick();
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

        int tileEntityRadius = (Math.max(tileEntityConfig.getRaycastRadius(), Bukkit.getViewDistance())+15)/16; //Fine to precompute this stuff cos a single division per tick is negligible

        for (int i = 0; i < batches.size(); i++) {
            int finalI = i;
            List<PlayerData> batch = batches.get(i);
            plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
                Thread.currentThread().setName("RaycastedAntiESP Engine TickProcessor "+ finalI + " - Folia Async Pool");
                processTickForPlayers(batch, entityConfig, playerConfig, tileEntityConfig, tileEntityRadius, debugConfig.showDebugParticles(), blockSnapshotManager, entitySnapshotManager, currentTick);
                task.cancel();
            });
        }
    }

    private void processTickForPlayers(List<PlayerData> playerDataList, EntityConfig entityConfig, PlayerConfig playerConfig, TileEntityConfig tileEntityConfig, int tileEntityRadius,
                                       boolean debugParticles, BlockSnapshotManager blockSnapshotManager, EntitySnapshotManager entitySnapshotManager, int currentTick) {

        for (PlayerData playerData : playerDataList) {
            if (playerData.hasBypassPermission()) continue;

            Locatable playerLocation = entitySnapshotManager.getLocation(playerData.getPlayerUUID());
            if (playerLocation == null) Logger.errorAndReturn(new RuntimeException("Player "+playerData.getPlayerUUID()+" does not have a location"), 3);

            if (entityConfig.isEnabled()) checkEntities(playerData, playerLocation, entityConfig, debugParticles, entitySnapshotManager, blockSnapshotManager, currentTick);
            if (playerConfig.isEnabled()) checkPlayers(playerData, playerLocation, playerConfig, debugParticles, entitySnapshotManager, blockSnapshotManager, currentTick);
            if (tileEntityConfig.isEnabled()) checkTileEntities(playerData, playerLocation, tileEntityConfig, tileEntityRadius, debugParticles, blockSnapshotManager, currentTick);
        }
    }
//TODO: Make sure player.getEntitiesNeedingRecheck is actually implemented
    private void checkEntities(PlayerData player, Locatable playerLocation, EntityConfig entityConfig, boolean debugParticles, EntitySnapshotManager entitySnapshotManager, BlockSnapshotManager blockSnapshotManager, int currentTick) {
        EntityVisibilityChanger entityVisibilityChanger = VisibilityChangeHandlers.getEntity();

        for (UUID entityUUID : player.entityVisibility().getNeedingRecheck(entityConfig.getVisibleRecheckIntervalTicks(), currentTick)) {
            Locatable entityLocation = entitySnapshotManager.getLocation(entityUUID);
            if (entityLocation == null) {
                return;
                //todo: add loggers to figure out why
            }
            boolean canSee = RaycastUtil.raycast(player, playerLocation, entityLocation, entityConfig.getMaxOccludingCount(), entityConfig.getAlwaysShowRadius(), entityConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1 /*TODO stop hardcoding*/);
            entityVisibilityChanger.setEntityVisibilityForPlayer(player.getPlayerUUID(), entityUUID, canSee, currentTick);
        }
    }

    private void checkPlayers(PlayerData player, Locatable playerLocation, PlayerConfig playerConfig, boolean debugParticles, EntitySnapshotManager entitySnapshotManager, BlockSnapshotManager blockSnapshotManager, int currentTick) {
        PlayerVisibilityChanger playerVisibilityChanger = VisibilityChangeHandlers.getPlayer();

        for (UUID otherPlayerUUID : player.playerVisibility().getNeedingRecheck(playerConfig.getVisibleRecheckIntervalTicks(), currentTick)) {
            Locatable otherPlayerLocation = entitySnapshotManager.getLocation(otherPlayerUUID);
            boolean canSee = RaycastUtil.raycast(player, playerLocation, otherPlayerLocation, playerConfig.getMaxOccludingCount(), playerConfig.getAlwaysShowRadius(), playerConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1 /*TODO stop hardcoding*/);
            playerVisibilityChanger.setPlayerVisibilityForPlayer(player.getPlayerUUID(), otherPlayerUUID, canSee, currentTick);
        }
    }

    private void checkTileEntities(PlayerData player, Locatable playerLocation, TileEntityConfig tileEntityConfig, int chunkRadius, boolean debugParticles, BlockSnapshotManager blockSnapshotManager, int currentTick) {
        //todo: How to get tile entities around player? Just chunkscan?
        TileEntityVisibilityChanger tileEntityVisibilityChanger = VisibilityChangeHandlers.getTileEntity();
        int chunkX = playerLocation.blockX() >> 4;
        int chunkZ = playerLocation.blockZ() >> 4;

        Set<AbstractBlockLocation> tileEntitiesToCheck = player.tileVisibility().getNeedingRecheck(tileEntityConfig.getVisibleRecheckIntervalTicks(), currentTick, playerLocation.world(), chunkX, chunkZ, chunkRadius, blockSnapshotManager);

        for (AbstractBlockLocation tileEntityLocation : tileEntitiesToCheck) {
            if (!player.tileVisibility().containsChunk(tileEntityLocation)) continue;
            boolean canSee = RaycastUtil.raycast(player, playerLocation, tileEntityLocation, tileEntityConfig.getMaxOccludingCount() + 1, tileEntityConfig.getAlwaysShowRadius(), tileEntityConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1 /*TODO stop hardcoding*/);
            tileEntityVisibilityChanger.setTileEntityVisibilityForPlayer(player.getPlayerUUID(), tileEntityLocation, canSee, currentTick);
        }
    }

    private void forceEntityLocationUpdate() {  //todo: Quite frankly idk if this is needed
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

        SnapshotManager.getEntitySnapshotManager().processEntityLocationQueue();
        asyncScheduler.runDelayed(plugin, this::processEntityMovements, 15, TimeUnit.MILLISECONDS);
    }

    private void flushLogCache(ScheduledTask scheduledTask) {
        if (config.getDebugConfig().logToFile()) {
            Logger.flush();
        }
        asyncScheduler.runDelayed(plugin, this::flushLogCache, 2, TimeUnit.SECONDS);
    }
}
