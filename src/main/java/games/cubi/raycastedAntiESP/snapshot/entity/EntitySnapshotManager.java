package games.cubi.raycastedAntiESP.snapshot.entity;

import games.cubi.raycastedAntiESP.locatables.Locatable;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;

import java.util.UUID;

public interface EntitySnapshotManager {
    Locatable getLocation(UUID entityUUID);
    SnapshotManager.EntitySnapshotManagerType getType();
}
