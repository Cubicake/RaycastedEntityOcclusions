package games.cubi.raycastedantiesp.paper;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedantiesp.paper.engine.PaperSimpleEngine;
import games.cubi.raycastedantiesp.paper.packets.PacketProcessor;
import games.cubi.raycastedantiesp.paper.data.DataHolder;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static games.cubi.raycastedantiesp.paper.UpdateChecker.checkForUpdates;

public class EventListener implements Listener {
    private final PacketProcessor packetProcessor;
    private final RaycastedAntiESP plugin;
    private final PaperSimpleEngine engine;

    private static EventListener instance = null;

    private EventListener(RaycastedAntiESP plugin, PacketProcessor packetProcessor, PaperSimpleEngine engine) {
        this.plugin = plugin;
        this.packetProcessor = packetProcessor;
        this.engine = engine;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static EventListener initialise(RaycastedAntiESP plugin, PacketProcessor packetProcessor, PaperSimpleEngine engine) {
        if (instance == null) {
            instance = new EventListener(plugin, packetProcessor, engine);
        }
        return instance;
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        PlayerRegistry.getInstance().unregisterPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST) //Runs first
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("raycastedantiesp.updatecheck")) { //todo: centralise permission strings to prevent issues when perm names are changed
            checkForUpdates(plugin, player);
        }

        boolean hasBypassPermission = player.hasPermission("raycastedantiesp.bypass");
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            PlayerRegistry.getInstance().registerPlayer(player.getUniqueId(), hasBypassPermission, DataHolder.getTick());
            playerData = PlayerRegistry.getInstance().getPlayerData(player.getUniqueId());
        }

        if (playerData == null) return;
        playerData.setBypassPermission(hasBypassPermission);
        updateOwnSnapshot(playerData, player.getEntityId(), player.getEyeLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(event.getPlayer().getUniqueId());
        if (playerData == null) return;
        updateOwnSnapshot(playerData, event.getPlayer().getEntityId(), event.getPlayer().getEyeLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChunkLoad(PlayerChunkLoadEvent e) {
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(e.getPlayer().getUniqueId());
        if (playerData == null) return;
        playerData.tileVisibility().addChunk(e.getChunk().getX(), e.getChunk().getZ());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChunkUnload(PlayerChunkUnloadEvent e) {
        PlayerData player = PlayerRegistry.getInstance().getPlayerData(e.getPlayer().getUniqueId());
        if (player == null) return; // They've logged out
        player.tileVisibility().removeChunk(e.getWorld().getUID(), e.getChunk().getX(), e.getChunk().getZ());
    }

    @EventHandler(priority = EventPriority.LOWEST) //Runs first
    public void serverTickStartEvent(ServerTickStartEvent event) {
        if (VisibilityChangeHandlers.entityVisibilityChangeHandlerType() == VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getEntity().processCache();

        if (VisibilityChangeHandlers.playerVisibilityChangeHandlerType() == VisibilityChangeHandlers.PlayerVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getPlayer().processCache();

        if (VisibilityChangeHandlers.tileEntityVisibilityChangeHandlerType() == VisibilityChangeHandlers.TileEntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getTileEntity().processCache();

        DataHolder.incrementTick();

        Bukkit.getAsyncScheduler().runNow(plugin, task -> engine.tick());
    }

    @EventHandler(priority = EventPriority.MONITOR) //Runs last
    public void serverTickStopEvent(ServerTickEndEvent event) {
        if (VisibilityChangeHandlers.entityVisibilityChangeHandlerType() == VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getEntity().processCache();

        if (VisibilityChangeHandlers.playerVisibilityChangeHandlerType() == VisibilityChangeHandlers.PlayerVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getPlayer().processCache();

        if (VisibilityChangeHandlers.tileEntityVisibilityChangeHandlerType() == VisibilityChangeHandlers.TileEntityVisibilityChangerType.BUKKIT) VisibilityChangeHandlers.getTileEntity().processCache();

    }

    private void updateOwnSnapshot(PlayerData playerData, int entityId, Location location) {
        if (SnapshotManager.entitySnapshotManagerType() != SnapshotManager.SnapshotManagerType.PACKETEVENTS) {
            return;
        }

        playerData.entitySnapshotManager().upsertEntity(
                entityId,
                playerData.getPlayerUUID(),
                location.getWorld().getUID(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

}
