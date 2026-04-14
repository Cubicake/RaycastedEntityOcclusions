package games.cubi.raycastedantiesp.core.packets.api;

import java.util.UUID;

/**
 * Generic input contract for any packet backend (IE PacketEvents). Cannot currently be moved to PlayerEntitySnapshotManager due to the use of entity ID rather than UUID (but maybe generics could be used to resolve this?).
 */
public interface EntitySnapshotPacketSink {
    void upsertEntity(int entityID, UUID entityUUID, UUID world, double x, double y, double z);

    void moveRelative(int entityID, double deltaX, double deltaY, double deltaZ);

    void moveAbsolute(int entityID, double x, double y, double z);

    void removeEntity(int entityID);

    void clear();
}
