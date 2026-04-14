package games.cubi.raycastedantiesp.packetevents;

import games.cubi.raycastedantiesp.core.packets.core.PacketEntitySnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;

public class PacketEventsEntitySnapshotManager extends PacketEntitySnapshotManager {
    @Override
    public SnapshotManager.SnapshotManagerType getType() {
        return SnapshotManager.SnapshotManagerType.PACKETEVENTS;
    }
}
