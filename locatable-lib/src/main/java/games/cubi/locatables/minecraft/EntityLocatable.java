package games.cubi.locatables.minecraft;

import games.cubi.locatables.MutableLocatable;

import java.util.List;
import java.util.UUID;

/**
 * Per-player platform-independent representation of an entity
 */
public interface EntityLocatable<EntityType, Direction> extends MutableLocatable {
    enum SpawnType {
        LIVING,
        ENTITY,
        PAINTING,
        PLAYER,
    }

    int entityID();

    UUID entityUUID();

    boolean visible();
    EntityLocatable setVisible(boolean visible);

    int lastChecked();
    EntityLocatable setLastChecked(int lastChecked);

    boolean clientVisible();
    EntityLocatable setClientVisible(boolean clientVisible);

    SpawnType spawnType();

    float yaw();
    EntityLocatable setYaw(float yaw);

    float pitch();
    EntityLocatable setPitch(float pitch);

    float headYaw();
    EntityLocatable setHeadYaw(float headYaw);

    double velocityX();
    double velocityY();
    double velocityZ();
    EntityLocatable setVelocity(double velocityX, double velocityY, double velocityZ);

    boolean onGround();
    EntityLocatable setOnGround(boolean onGround);

    EntityType entityType();

    int entityData();
    EntityLocatable setEntityData(int entityData);

    EntityType paintingType();

    Direction paintingDirection();

    List<?> metadata();
    EntityLocatable<?, ?> setMetadata(List<?> metadata);

    List<?> equipment();
    EntityLocatable<?, ?> setEquipment(List<?> equipment);

    int[] passengerIDs();
    EntityLocatable<?, ?> setPassengerIDs(int[] passengerIDs);

    Object packetReplayData();
    EntityLocatable<?, ?> setPacketReplayData(Object packetReplayData);

    default <T> T cast() {
        return (T) this;
    }
}
