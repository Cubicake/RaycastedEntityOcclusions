package games.cubi.raycastedantiesp.core.packets.api;

import java.util.Set;
import java.util.UUID;

/**
 * Generic input contract for block/world snapshot ingestion. TODO: May be able to be fully moved into PlayerBlockSnapshotManager
 */
public interface BlockSnapshotPacketSink {
    void upsertBlock(UUID world, int x, int y, int z, boolean occluding, boolean tileEntity);

    void removeChunk(UUID world, int chunkX, int chunkZ);

    void replaceChunk(UUID world, int chunkX, int chunkY, int chunkZ, Set<Short> occludingPackedPositions, Set<Short> tilePackedPositions);

    void clear();
}
