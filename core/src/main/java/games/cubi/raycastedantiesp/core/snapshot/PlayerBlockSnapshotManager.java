package games.cubi.raycastedantiesp.core.snapshot;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.packets.api.BlockSnapshotPacketSink;

import java.util.Set;
import java.util.UUID;

public interface PlayerBlockSnapshotManager extends BlockSnapshotPacketSink {
    boolean isBlockOccluding(BlockLocatable location);
    Set<ImmutableBlockLocatable> getKnownTileEntities();

    interface Factory {
        PlayerBlockSnapshotManager createPlayerBlockSnapshotManager();
    }

    default void upsertBlock(UUID world, int x, int y, int z, boolean occluding, boolean tileEntity) {
        Logger.errorAndReturn(new UnsupportedOperationException("Not implemented"), 2, PlayerBlockSnapshotManager.class);
    }

    default void removeChunk(UUID world, int chunkX, int chunkZ) {
        Logger.errorAndReturn(new UnsupportedOperationException("Not implemented"), 2, PlayerBlockSnapshotManager.class);
    }

    default void replaceChunk(UUID world, int chunkX, int chunkY, int chunkZ, Set<Short> occludingPackedPositions, Set<Short> tilePackedPositions) {
        Logger.errorAndReturn(new UnsupportedOperationException("Not implemented"), 2, PlayerBlockSnapshotManager.class);
    }

    default void clear() {
        Logger.errorAndReturn(new UnsupportedOperationException("Not implemented"), 2, PlayerBlockSnapshotManager.class);
    }
}
