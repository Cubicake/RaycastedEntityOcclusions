package games.cubi.raycastedantiesp.core.snapshot;

import games.cubi.locatables.Locatable;

import java.util.UUID;

public interface EntitySnapshotManager {
    Locatable getLocation(UUID entityUUID);

    SnapshotManager.EntitySnapshotManagerType getType();

    void removeEntityLocation(UUID entityUUID); //for use when entity is unloaded, fine not to queue as this will be rare
    void processEntityLocationQueue();
}
