package games.cubi.raycastedantiesp.packetevents.locatables;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.world.Direction;
import com.github.retrooper.packetevents.protocol.world.PaintingType;
import games.cubi.raycastedantiesp.core.locatables.NettyEntityLocatable;
import games.cubi.raycastedantiesp.packetevents.replaydata.PacketEventsEntityReplayData;

import java.util.UUID;

public class PacketEventsEntity extends NettyEntityLocatable<EntityType, PaintingType, Direction, PacketEventsEntityReplayData> {
    public PacketEventsEntity(UUID world, double x, double y, double z, int entityID, UUID entityUUID, SpawnType spawnType, EntityType entityType, boolean visible) {
        super(world, x, y, z, entityID, entityUUID, spawnType, entityType, visible);
    }

    public PacketEventsEntity(UUID world, double x, double y, double z, int entityID, UUID entityUUID, SpawnType spawnType, PaintingType paintingType, Direction paintingDirection, boolean visible) {
        super(world, x, y, z, entityID, entityUUID, spawnType, paintingType, paintingDirection, visible);
    }

    @Override
    public boolean strictlyEquals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PacketEventsEntity that)) return false;
        if (!this.equals(other)) return false;

        if (entityID() != that.entityID()) return false;
        if (!entityUUID().equals(that.entityUUID())) return false;
        if (spawnType() != that.spawnType()) return false;
        if (entityType() != null ? !entityType().equals(that.entityType()) : that.entityType() != null) return false;
        if (paintingType() != null ? !paintingType().equals(that.paintingType()) : that.paintingType() != null)
            return false;
        return paintingDirection() == that.paintingDirection();
    }
}
