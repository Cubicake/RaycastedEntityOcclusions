package games.cubi.raycastedantiesp.paper.staging;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.PlayerEntitySnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsBlockSnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsEntitySnapshotManager;
import games.cubi.raycastedantiesp.packetevents.PacketEventsSnapshotBridge;

public class PaperPESnapshotFactory extends PacketEventsSnapshotBridge {

    public PaperPESnapshotFactory() {
        super(new PacketEventsPaperBlockInfoResolver());
    }

    @Override
    protected void registerListener() {
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.MONITOR);
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
