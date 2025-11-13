package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.utils.Locatable;

import java.util.UUID;

public interface EntitySnapshotManager {
    public Locatable getLocation(UUID entityUUID);
}
