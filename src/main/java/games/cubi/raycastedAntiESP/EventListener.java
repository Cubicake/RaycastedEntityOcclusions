package games.cubi.raycastedAntiESP;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import games.cubi.raycastedAntiESP.packets.PacketProcessor;
import games.cubi.raycastedAntiESP.snapshot.ChunkSnapshotManager;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.config.ConfigManager;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.UUID;

import static games.cubi.raycastedAntiESP.UpdateChecker.checkForUpdates;

public class EventListener implements Listener {
    private final ChunkSnapshotManager manager;
    private final ConfigManager config;
    private PacketProcessor packetProcessor;
    private final RaycastedAntiESP plugin;

    private static EventListener instance = null;

    private EventListener(RaycastedAntiESP plugin, ChunkSnapshotManager mgr, ConfigManager cfg) {
        this.manager = mgr;
        this.config = cfg;
        this.plugin = plugin;
        //load packet processor after a tick in a bukkit runnable to ensure the plugin is fully loaded TODO: All schedulers should migrate to paper/folia scheduler, also this should be moved somewhere else, maybe when the config gets the update it passes it on?
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (DataHolder.isPacketEventsPresent()) {
                packetProcessor = RaycastedAntiESP.getPacketProcessor();
            } else {
                packetProcessor = null;
            }
        }, 1L);
    }

    public static EventListener getInstance(RaycastedAntiESP plugin, ChunkSnapshotManager mgr, ConfigManager cfg) {
        if (instance == null) {
            instance = new EventListener(plugin, mgr, cfg);
        }
        return instance;
    }

    // Snapshot events

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent e) {
        manager.onChunkLoad(e.getChunk());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent e) {
        manager.onChunkUnload(e.getChunk());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent e) {
        manager.onBlockChange(e.getBlock().getLocation(), e.getBlock().getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e) {
        manager.onBlockChange(e.getBlock().getLocation(), Material.AIR);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBurn(BlockBurnEvent e) {
        manager.onBlockChange(e.getBlock().getLocation(), Material.AIR);
    }
    // These events do not cover all cases, but I can't be bothered to figure out a better solution rn. Frequent snapshot refreshes is the solution. If anyone has a solution please let me know.


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        if (DataHolder.isPacketEventsPresent() && packetProcessor != null) {
            UUID player = e.getPlayer().getUniqueId();
            packetProcessor.sendPlayerInfoRemovePacket(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("raycastedentityocclusions.updatecheck")) {
            checkForUpdates(plugin, player);
        }
        DataHolder.players().registerPlayer(player.getUniqueId(), player.hasPermission("raycastedentityocclusions.bypass"));
    }

    @EventHandler(priority = EventPriority.LOWEST) //Runs first
    public void serverTickStartEvent(ServerTickStartEvent event) {
        DataHolder.incrementTick();
        //TODO: connect this to new engine
    }

    @EventHandler(priority = EventPriority.MONITOR) //Runs last
    public void serverTickStopEvent(ServerTickStartEvent event) {
        DataHolder.incrementTick();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityMove(EntityMoveEvent event) {
        DataHolder.entityLocation().queueEntityLocationUpdate(event.getEntity().getUniqueId(), event.getTo().add(0,event.getEntity().getHeight()/2,0));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTrackEntity(PlayerTrackEntityEvent event) {
        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (DataHolder.entityVisibility().entityVisibilityShouldChange(entityUUID, playerUUID)) return; // The player is meant to see the entity, do nothing

        event.setCancelled(true);
        DataHolder.entityVisibility().hideEntity(entityUUID, player);
        DataHolder.players().getPlayerData(playerUUID).addEntity(entityUUID);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerUntrackEntity(PlayerTrackEntityEvent event) {
        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (DataHolder.entityVisibility().entityVisibilityShouldChange(entityUUID, playerUUID)) return; // State change was triggered by us, do nothing

        DataHolder.players().getPlayerData(playerUUID).removeEntity(entityUUID); // Remove entity from player's data as they are no longer tracking it, so no more raycasts are needed
    }

}