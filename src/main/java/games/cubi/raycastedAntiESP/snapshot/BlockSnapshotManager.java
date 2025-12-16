package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;

public interface BlockSnapshotManager {
    boolean isBlockOccluding(AbstractBlockLocation location);
}
