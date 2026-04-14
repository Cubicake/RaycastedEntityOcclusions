package games.cubi.raycastedantiesp.core.snapshot;

import games.cubi.locatables.Locatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.packets.api.EntitySnapshotPacketSink;

import java.util.UUID;

public interface PlayerEntitySnapshotManager extends EntitySnapshotPacketSink {
    Locatable getLocation(UUID entityUUID);

    SnapshotManager.SnapshotManagerType getType();

    void removeEntityLocation(UUID entityUUID);

    interface Factory {
        PlayerEntitySnapshotManager createPlayerEntitySnapshotManager();
        SnapshotManager.SnapshotManagerType getType();
    }

    default void upsertEntity(int entityID, UUID entityUUID, UUID world, double x, double y, double z) {
        Logger.errorAndReturn(new UnsupportedOperationException("Not a packet snapshot manager"), 2, PlayerEntitySnapshotManager.class);
    }

    default void moveRelative(int entityID, double deltaX, double deltaY, double deltaZ) {
        Logger.errorAndReturn(new UnsupportedOperationException("Not a packet snapshot manager"), 2, PlayerEntitySnapshotManager.class);
    }

    default void moveAbsolute(int entityID, double x, double y, double z) {
        Logger.errorAndReturn(new UnsupportedOperationException("Not a packet snapshot manager"), 2, PlayerEntitySnapshotManager.class);
    }

    default void removeEntity(int entityID) {
        Logger.errorAndReturn(new UnsupportedOperationException("Not a packet snapshot manager"), 2, PlayerEntitySnapshotManager.class);
    }

    default void clear() {
        Logger.errorAndReturn(new UnsupportedOperationException("Not a packet snapshot manager"), 2, PlayerEntitySnapshotManager.class);
    }
}
