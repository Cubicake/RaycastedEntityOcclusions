package games.cubi.raycastedantiesp.paper.staging;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.PlayerEntitySnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsBlockSnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsEntitySnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsSnapshotBridge;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaperPESnapshotFactory extends PacketEventsSnapshotBridge implements Listener {
    private final Map<UUID, DimensionType> dimensionTypeByWorldId = new ConcurrentHashMap<>();

    public PaperPESnapshotFactory() {
        super(new PacketEventsPaperBlockInfoResolver());
        for (var world : Bukkit.getWorlds()) {
            registerWorld(world);
        }
        Bukkit.getPluginManager().registerEvents(this, RaycastedAntiESP.get());
    }

    @Override
    protected void registerListener() {
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.MONITOR);
    }

    @Override
    protected UUID resolveWorldUUID(User user) {
        DimensionType dimensionType = user.getDimensionType();
        for (Map.Entry<UUID, DimensionType> entry : dimensionTypeByWorldId.entrySet()) {
            if (entry.getValue().equals(dimensionType)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        registerWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        dimensionTypeByWorldId.remove(event.getWorld().getUID());
    }

    private void registerWorld(World world) {
        dimensionTypeByWorldId.put(world.getUID(), SpigotConversionUtil.typeFromBukkitWorld(world));
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
