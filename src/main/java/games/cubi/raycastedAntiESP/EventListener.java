package games.cubi.raycastedAntiESP;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import games.cubi.raycastedAntiESP.engine.Engine;
import games.cubi.raycastedAntiESP.packets.PacketEventsStatus;
import games.cubi.raycastedAntiESP.packets.PacketProcessor;
import games.cubi.raycastedAntiESP.snapshot.block.BukkitBSM;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.entity.BukkitESM;
import games.cubi.raycastedAntiESP.utils.PlayerData;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import static games.cubi.raycastedAntiESP.UpdateChecker.checkForUpdates;

public class EventListener implements Listener {
    private final ConfigManager config;
    private final PacketProcessor packetProcessor;
    private final RaycastedAntiESP plugin;

    private static EventListener instance = null;

    private EventListener(RaycastedAntiESP plugin, ConfigManager cfg, PacketProcessor packetProcessor) {
        this.config = cfg;
        this.plugin = plugin;
        this.packetProcessor = packetProcessor;
    }

    public static EventListener getInstance(RaycastedAntiESP plugin, ConfigManager cfg, PacketProcessor packetProcessor) {
        if (instance == null) {
            instance = new EventListener(plugin, cfg, packetProcessor);
        }
        return instance;
    }

    // Snapshot events

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent e) {
        BukkitBSM manager = bukkitBlockSnapshotManager();
        if (manager == null) return;
        manager.onChunkLoad(e.getChunk());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent e) {
        BukkitBSM manager = bukkitBlockSnapshotManager();
        if (manager == null) return;
        manager.onChunkUnload(e.getChunk());
    }

    public static final int BREAK = 1; public static final int PLACE = 2;
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent e) {
        BukkitBSM manager = bukkitBlockSnapshotManager();
        if (manager == null) return;
        manager.onBlockChange(e.getBlock().getLocation(), e.getBlock().getType(), PLACE);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e) {
        BukkitBSM manager = bukkitBlockSnapshotManager();
        if (manager == null) return;
        manager.onBlockChange(e.getBlock().getLocation(), Material.AIR, BREAK);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBurn(BlockBurnEvent e) {
        BukkitBSM manager = bukkitBlockSnapshotManager();
        if (manager == null) return;
        manager.onBlockChange(e.getBlock().getLocation(), Material.AIR, BREAK);
    }
    // These events do not cover all cases, but I can't be bothered to figure out a better solution rn. Frequent snapshot refreshes is the solution. If anyone has a solution please let me know. todo: listen to blockphysicsevent


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        if (PacketEventsStatus.get().isPacketEventsPresent() && packetProcessor != null) {
            UUID player = e.getPlayer().getUniqueId();
            packetProcessor.sendPlayerInfoRemovePacket(player);
        }
        if (isBukkitESM()) {
            bukkitEntitySnapshotManager().untrackPlayer(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("raycastedantiesp.updatecheck")) { //todo: centralise permission strings to prevent issues when perm names are changed
            checkForUpdates(plugin, player);
        }
        DataHolder.players().registerPlayer(player.getUniqueId(), player.hasPermission("raycastedantiesp.bypass"));

        if (SnapshotManager.entitySnapshotManagerType() == SnapshotManager.EntitySnapshotManagerType.BUKKIT) {
            updateEntityLocation(player.getUniqueId(), player.getEyeLocation());
            bukkitEntitySnapshotManager().trackPlayer(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) //Runs first
    public void serverTickStartEvent(ServerTickStartEvent event) {
        if (VisibilityChangeHandlers.entityVisibilityChangeHandlerType() == VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getEntity().processCache();

        if (VisibilityChangeHandlers.playerVisibilityChangeHandlerType() == VisibilityChangeHandlers.PlayerVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getPlayer().processCache();

        if (VisibilityChangeHandlers.tileEntityVisibilityChangeHandlerType() == VisibilityChangeHandlers.TileEntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getTileEntity().processCache();

        DataHolder.incrementTick();

        Bukkit.getAsyncScheduler().runNow(plugin, task -> RaycastedAntiESP.getEngine().distributeTick());
        RaycastedAntiESP.getEngine().distributeTick();
    }

    @EventHandler(priority = EventPriority.MONITOR) //Runs last
    public void serverTickStopEvent(ServerTickEndEvent event) {
        if (VisibilityChangeHandlers.entityVisibilityChangeHandlerType() == VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getEntity().processCache();

        if (VisibilityChangeHandlers.playerVisibilityChangeHandlerType() == VisibilityChangeHandlers.PlayerVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getPlayer().processCache();

        if (VisibilityChangeHandlers.tileEntityVisibilityChangeHandlerType() == VisibilityChangeHandlers.TileEntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getTileEntity().processCache();

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityMove(EntityMoveEvent event) {
        updateEntityLocation(event.getEntity().getUniqueId(), event.getTo().clone().add(0, event.getEntity().getHeight() / 2, 0));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        updateEntityLocation(event.getPlayer().getUniqueId(), event.getPlayer().getEyeLocation());
    }

    private void updateEntityLocation(UUID entityUUID, Location newLocation) {
        if (!(SnapshotManager.entitySnapshotManagerType() == SnapshotManager.EntitySnapshotManagerType.BUKKIT)) {
            return;
        }
        BukkitESM bukkitESM = (BukkitESM) SnapshotManager.getEntitySnapshotManager();
        bukkitESM.queueEntityLocationUpdate(entityUUID, newLocation);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTrackEntity(PlayerTrackEntityEvent event) {
        //todo: This is probably specific to bukkit, add a check for that once other implementations exist
        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        PlayerData playerData = DataHolder.players().getPlayerData(playerUUID);

        if (playerData == null || playerData.isEntityVisible(entityUUID)) return; // The player is meant to see the entity, do nothing

        event.setCancelled(true);
        if (event.getEntity() instanceof Player) {
            VisibilityChangeHandlers.getPlayer().hidePlayerFromPlayer(playerUUID, entityUUID);
        } else {
            VisibilityChangeHandlers.getEntity().hideEntityFromPlayer(playerUUID, entityUUID);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerUntrackEntity(PlayerUntrackEntityEvent event) {
        //todo: Same as above, probs specific to Bukkit
        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = DataHolder.players().getPlayerData(playerUUID);
        if ((playerData == null) || (!playerData.isEntityVisible(entityUUID))) return; // State change was triggered by us, do nothing

        DataHolder.players().getPlayerData(playerUUID).removeEntity(entityUUID); // Remove entity from player's data as they are no longer tracking it, so no more raycasts are needed
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (isBukkitESM()) bukkitEntitySnapshotManager().queueEntityLocationUpdate(event.getEntity().getUniqueId(), event.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        if (!isBukkitESM()) return;
        for (var entity : event.getEntities()) {
            bukkitEntitySnapshotManager().queueEntityLocationUpdate(entity.getUniqueId(), entity.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        if (!isBukkitESM()) return;
        for (var entity : event.getEntities()) {
            bukkitEntitySnapshotManager().removeEntityLocation(entity.getUniqueId());
        }
    }

    private @Nullable BukkitBSM bukkitBlockSnapshotManager() {
        if (!(SnapshotManager.blockSnapshotManagerType() == SnapshotManager.BlockSnapshotManagerType.BUKKIT)) {
            return null;
        }
        return (BukkitBSM) SnapshotManager.getBlockSnapshotManager();
    }

    private @Nullable BukkitESM bukkitEntitySnapshotManager() {
        if (!(SnapshotManager.entitySnapshotManagerType() == SnapshotManager.EntitySnapshotManagerType.BUKKIT)) {
            return null;
        }
        return (BukkitESM) SnapshotManager.getEntitySnapshotManager();
    }

    private boolean isBukkitESM() {
        return SnapshotManager.entitySnapshotManagerType() == SnapshotManager.EntitySnapshotManagerType.BUKKIT;
    }
}
