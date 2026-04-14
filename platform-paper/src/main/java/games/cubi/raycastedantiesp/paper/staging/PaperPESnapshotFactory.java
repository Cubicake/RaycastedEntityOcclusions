package games.cubi.raycastedantiesp.paper.staging;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.player.User;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.PlayerEntitySnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsBlockSnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsEntitySnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsSnapshotBridge;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaperPESnapshotFactory extends PacketEventsSnapshotBridge implements Listener {
    private final Map<NamespacedKey, UUID> worldIdByWorldKey = new ConcurrentHashMap<>();

    public PaperPESnapshotFactory() {
        super(new PacketEventsPaperBlockInfoResolver());
        Bukkit.getPluginManager().registerEvents(this, RaycastedAntiESP.get());
        Bukkit.getWorlds().forEach(this::registerWorld);
    }

    @Override
    protected void registerListener() {
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.MONITOR);
    }

    @Override
    protected UUID resolveWorldUUID(User user) {
        if (user.getDimensionType() == null || user.getDimensionType().getName() == null) {
            Logger.warning("User " + user.getName() + " has null dimension type or dimension type name.", 3, PaperPESnapshotFactory.class);
            return null;
        }

        NamespacedKey worldKey = NamespacedKey.fromString(user.getDimensionType().getName().toString());
        if (worldKey == null) {
            Logger.warning("User " + user.getName() + " has invalid dimension type name: " + user.getDimensionType().getName(), 3, PaperPESnapshotFactory.class);
            return null;
        }
        return worldIdByWorldKey.get(worldKey);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        registerWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        worldIdByWorldKey.remove(event.getWorld().getKey());
    }

    private void registerWorld(World world) {
        worldIdByWorldKey.put(world.getKey(), world.getUID());
    }

    @Override
    public PlayerBlockSnapshotManager createPlayerBlockSnapshotManager() {
        return new PacketEventsBlockSnapshotManager();
    }

    @Override
    public PlayerEntitySnapshotManager createPlayerEntitySnapshotManager() {
        return new PacketEventsEntitySnapshotManager();
    }

    @Override
    public SnapshotManager.SnapshotManagerType getType() {
        return SnapshotManager.SnapshotManagerType.PACKETEVENTS;
    }
}
