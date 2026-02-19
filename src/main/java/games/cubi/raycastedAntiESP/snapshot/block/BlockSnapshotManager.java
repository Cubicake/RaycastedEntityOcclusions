package games.cubi.raycastedAntiESP.snapshot.block;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;

import java.util.Set;
import java.util.UUID;

public interface BlockSnapshotManager {
    boolean isBlockOccluding(AbstractBlockLocation location);
    Set<BlockLocation> getTileEntitiesInChunk(UUID world, int x, int z);
    SnapshotManager.BlockSnapshotManagerType getType();
}
