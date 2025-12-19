package games.cubi.raycastedAntiESP.snapshot.block;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;

public interface BlockSnapshotManager {
    boolean isBlockOccluding(AbstractBlockLocation location);
    SnapshotManager.BlockSnapshotManagerType getType();
}
