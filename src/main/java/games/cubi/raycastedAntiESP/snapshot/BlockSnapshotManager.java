package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.utils.Locatable;
import org.bukkit.Material;

public interface BlockSnapshotManager {
    public Material getMaterialAt(Locatable location);
}
