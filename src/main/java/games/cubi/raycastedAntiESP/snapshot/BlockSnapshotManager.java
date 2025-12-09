package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import org.bukkit.Material;

public interface BlockSnapshotManager {
    Material getMaterialAt(AbstractBlockLocation location);
}
