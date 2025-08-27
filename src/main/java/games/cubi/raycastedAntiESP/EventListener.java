package games.cubi.raycastedAntiESP;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import games.cubi.raycastedAntiESP.engine.Engine;
import games.cubi.raycastedAntiESP.packets.PacketProcessor;
import games.cubi.raycastedAntiESP.snapshot.ChunkSnapshotManager;
import games.cubi.raycastedAntiESP.utils.DataHolder;
import games.cubi.raycastedAntiESP.config.ConfigManager;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    private final Engine engine;

    public EventListener(RaycastedAntiESP plugin, ChunkSnapshotManager mgr, ConfigManager cfg, Engine engine) {
        this.manager = mgr;
        this.config = cfg;
        this.plugin = plugin;
        this.engine = engine;
        //load packet processor after a tick in a bukkit runnable to ensure the plugin is fully loaded TODO: All schedulers should migrate to paper/folia scheduler, also this should be moved somewhere else, maybe when the config gets the update it passes it on?
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (DataHolder.packetEventsPresent) {
                packetProcessor = RaycastedAntiESP.getPacketProcessor();
            } else {
                packetProcessor = null;
            }
        }, 1L);
    }

    // Snapshot events

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        manager.onChunkLoad(e.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        manager.onChunkUnload(e.getChunk());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        manager.onBlockChange(e.getBlock().getLocation(), e.getBlock().getType());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        manager.onBlockChange(e.getBlock().getLocation(), Material.AIR);
    }

    @EventHandler
    public void onBurn(BlockBurnEvent e) {
        manager.onBlockChange(e.getBlock().getLocation(), Material.AIR);
    }
    // These events do not cover all cases, but I can't be bothered to figure out a better solution rn. Frequent snapshot refreshes is the solution. If anyone has a solution please let me know.


    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        if (DataHolder.packetEventsPresent && packetProcessor != null) {
            UUID player = e.getPlayer().getUniqueId();
            packetProcessor.sendPlayerInfoRemovePacket(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("raycastedentityocclusions.updatecheck")) {
            checkForUpdates(plugin, player);
        }
        DataHolder.registerPlayer(player.getUniqueId(), player.hasPermission("raycastedentityocclusions.bypass"));
    }

    @EventHandler
    public void serverTickStartEvent(ServerTickStartEvent event) {
        //TODO: connect this to new engine
    }

    @EventHandler
    public void serverTickStopEvent(ServerTickStartEvent event) {

    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        DataHolder.setOrUpdateEntityLocation(event.getEntity().getUniqueId(), event.getTo());
    }
}