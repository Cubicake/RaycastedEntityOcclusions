package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.locatables.Locatable;
import org.bukkit.Material;

public interface BlockSnapshotManager {
    Material getMaterialAt(Locatable location);
}
