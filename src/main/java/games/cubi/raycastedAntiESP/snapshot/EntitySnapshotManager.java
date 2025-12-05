package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.locatables.Locatable;

import java.util.UUID;

public interface EntitySnapshotManager {
    Locatable getLocation(UUID entityUUID);
}
