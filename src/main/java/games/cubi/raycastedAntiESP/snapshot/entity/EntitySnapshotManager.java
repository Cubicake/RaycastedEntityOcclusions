package games.cubi.raycastedAntiESP.snapshot.entity;

import games.cubi.raycastedAntiESP.locatables.Locatable;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import org.bukkit.Location;

import java.util.UUID;

public interface EntitySnapshotManager {
    Locatable getLocation(UUID entityUUID);

    SnapshotManager.EntitySnapshotManagerType getType();

    void queueEntityLocationUpdate(UUID entityUUID, Location location);
    void removeEntityLocation(UUID entityUUID); //for use when entity is unloaded, fine not to queue as this will be rare
    void processEntityLocationQueue();
}
