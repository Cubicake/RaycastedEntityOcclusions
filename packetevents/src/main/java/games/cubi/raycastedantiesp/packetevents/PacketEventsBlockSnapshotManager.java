package games.cubi.raycastedantiesp.packetevents;

import games.cubi.raycastedantiesp.core.packets.core.PacketBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;

public class PacketEventsBlockSnapshotManager extends PacketBlockSnapshotManager {
    @Override
    public SnapshotManager.SnapshotManagerType getType() {
        return SnapshotManager.SnapshotManagerType.PACKETEVENTS;
    }
}
