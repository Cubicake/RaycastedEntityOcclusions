package games.cubi.raycastedantiesp.core.packets.core;


import games.cubi.locatables.Locatable;
import games.cubi.locatables.implementations.ThreadSafeLocatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.packets.api.EntitySnapshotPacketSink;
import games.cubi.raycastedantiesp.core.snapshot.PlayerEntitySnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//Per-player
public abstract class PacketEntitySnapshotManager implements PlayerEntitySnapshotManager, EntitySnapshotPacketSink {
//todo: investigate FastUtil use
    private final Map<Integer, EntityLocation> byEntityId = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> idByUuid = new ConcurrentHashMap<>();

    @Override
    public Locatable getLocation(UUID entityUUID) {
        final Integer entityId = idByUuid.get(entityUUID);
        if (entityId == null) {
            return null;
        }

        final EntityLocation snapshot = byEntityId.get(entityId);
        if (snapshot == null) {
            return null;
        }

        return snapshot.location.clonePlainAndCentreIfBlockLocation(); // Just a clone method but I'm too lazy to actually implement cloneable
    }

    @Override
    public void removeEntityLocation(UUID entityUUID) {
        final Integer entityId = idByUuid.remove(entityUUID);
        if (entityId != null) {
            removeEntity(entityId);
        }
    }

    @Override
    public void upsertEntity(int entityId, UUID entityUuid, UUID world, double x, double y, double z) {
        final EntityLocation existing = byEntityId.get(entityId);
        if (existing != null) {
            existing.location.set(x, y + 0.5, z, world);

            if (entityUuid != null) {
                idByUuid.put(entityUuid, entityId);
            }
        } else {
            final ThreadSafeLocatable snapshot = new ThreadSafeLocatable(world, x, y + 0.5, z);
            byEntityId.put(entityId, new EntityLocation(entityUuid, snapshot));
            if (entityUuid != null) {
                idByUuid.put(entityUuid, entityId);
            }
        }
    }

    @Override
    public void moveRelative(int entityId, double deltaX, double deltaY, double deltaZ) {
        final EntityLocation existing = byEntityId.get(entityId);
        if (existing == null) {
            Logger.warning("Received relative movement for entity ID " + entityId + " which does not exist in snapshot manager. Ignoring.", 3, PacketEntitySnapshotManager.class);
            return;
        }

        existing.location.add(deltaX, deltaY, deltaZ);
    }

    @Override
    public void moveAbsolute(int entityId, double x, double y, double z) {
        final EntityLocation existing = byEntityId.get(entityId);
        if (existing == null) {
            return;
        }
        UUID world = existing.location.world();
        existing.location.set(x, y + 0.5, z, world);
    }

    @Override
    public void removeEntity(int entityId) {
        final EntityLocation removed = byEntityId.remove(entityId);
        if (removed != null && removed.entityUUID != null) {
            idByUuid.remove(removed.entityUUID);
        }
    }

    @Override
    public void clear() {
        byEntityId.clear();
        idByUuid.clear();
    }

    private record EntityLocation(UUID entityUUID, ThreadSafeLocatable location) {}
}
