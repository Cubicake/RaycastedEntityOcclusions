package games.cubi.raycastedantiesp.core.view.controller;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.ConfigManager;
import games.cubi.raycastedantiesp.core.config.raycast.EntityConfig;
import games.cubi.raycastedantiesp.core.config.raycast.PlayerConfig;
import games.cubi.raycastedantiesp.core.config.raycast.RaycastConfig;
import games.cubi.raycastedantiesp.core.locatables.EntityLocatable;
import games.cubi.raycastedantiesp.core.locatables.NettyEntityLocatable;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.view.EntityView;

import java.util.UUID;

/**
 * @param <P> The platform's packet wrapper (PacketWrapper<?>)
 */
public abstract class PacketEntityViewController<P> {
    protected EntityConfig entityConfig = null;
    protected PlayerConfig playerConfig = null;
    protected double hideOnSpawnEntityDistanceSquared = 0;
    protected double hideOnSpawnPlayerDistanceSquared = 0;

    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleLivingEntitySpawn(P packet, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            Logger.error(new RuntimeException("World null when handling spawn living entity packet, uuid=" + playerData.getPlayerUUID() + " tick=" + currentTick), 2, PacketEntityViewController.class);
            return true;
        }
        NettyEntityLocatable<?,?,?,?> entity = trackEntitySpawn(playerData, packet, world, currentTick, EntityLocatable.SpawnType.LIVING);

        if (!entity.visible() && entityConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnEntityDistanceSquared) {
                entity.setClientVisible(false);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        return false;
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntitySpawn(P packet, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            Logger.error(new RuntimeException("World null when handling spawn entity packet, uuid=" + playerData.getPlayerUUID() + " tick=" + currentTick), 2, PacketEntityViewController.class);
            return true;
        }

        NettyEntityLocatable<?,?,?,?> entity = trackEntitySpawn(playerData, packet, world, currentTick, EntityLocatable.SpawnType.ENTITY);

        if (!entity.visible() && entityConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnEntityDistanceSquared) {
                entity.setClientVisible(false);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        return false;
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handlePaintingSpawn(P packet, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            Logger.error(new RuntimeException("World null when handling spawn painting packet, uuid=" + playerData.getPlayerUUID() + " tick=" + currentTick), 2, PacketEntityViewController.class);
            return true;
        }
        NettyEntityLocatable<?,?,?,?> entity = trackEntitySpawn(playerData, packet, world, currentTick, EntityLocatable.SpawnType.PAINTING);

        if (!entity.visible() && entityConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnEntityDistanceSquared) {
                entity.setClientVisible(false);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        return false;
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handlePlayerSpawn(P packet, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            Logger.error(new RuntimeException("World null when handling spawn player packet, uuid=" + playerData.getPlayerUUID() + " tick=" + currentTick), 2, PacketEntityViewController.class);
            return true;
        }
        NettyEntityLocatable<?,?,?,?> entity = trackEntitySpawn(playerData, packet, world, currentTick, EntityLocatable.SpawnType.PLAYER);

        if (!entity.visible() && playerConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnPlayerDistanceSquared) {
                entity.setClientVisible(false);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        return false;
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleRelativeMove(P packet, PlayerData playerData, int currentTick) {
        int entityID = processRelativeMovePacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleRelativeMoveAndRotation(P packet, PlayerData playerData, int currentTick) {
        int entityID = processRelativeMoveAndRotationPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleTeleport(P packet, PlayerData playerData, int currentTick) {
        int entityID = processTeleportPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handlePositionSync(P packet, PlayerData playerData, int currentTick) {
        int entityID = processPositionSyncPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityRotation(P packet, PlayerData playerData, int currentTick) {
        int entityID = processRotationPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityHeadLook(P packet, PlayerData playerData, int currentTick) {
        int entityID = processHeadLookPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityMetadata(P packet, PlayerData playerData, int currentTick) {
        int entityID = cachePacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityEquipment(P packet, PlayerData playerData, int currentTick) {
        int entityID = cachePacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityVelocity(P packet, PlayerData playerData, int currentTick) {
        int entityID = processEntityVelocityPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityEffect(P packet, PlayerData playerData, int currentTick) {
        int entityID = cachePacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityPassengers(P packet, PlayerData playerData, int currentTick) {
        int entityID = processEntityPassengersPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleDestroyEntities(P packet, PlayerData playerData, int currentTick) {
        int entityID = processDestroyEntitiesPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }

    /**
     * @return Either the entity or player view for this player, depending on the entity ID
     */
    protected EntityView<?> viewFromEntityID(int entityID, PlayerData playerData) {
        EntityView<?> entityView = playerData.entityView();
        if (entityView.exists(entityID)) {
            return entityView;
        }
        if (playerData.playerView().exists(entityID)) {
            return playerData.playerView();
        }
        Logger.errorAndReturn(new RuntimeException("Could not find view for entityID=" + entityID + " uuid=" + playerData.getPlayerUUID()), 1, PacketEntityViewController.class);
        return null;
    }

    protected RaycastConfig getCorrectConfig(EntityView<?> entityView) {
        if (entityView.isPlayerView()) {
            return playerConfig;
        } else {
            return entityConfig;
        }
    }

    /**
     * @return True if the packet should be suppressed
     */
    protected boolean cancelIfEnabledAndHidden(int entityID, PlayerData playerData) {
        EntityView<?> entityView = viewFromEntityID(entityID, playerData);

        if (entityView.isVisible(entityID)) {
            return false;
        }

        return getCorrectConfig(entityView).enabled(); // If this statement is reached, the entity should be hidden, so if the config is enabled it is hidden.
    }

    /**
     * @return The created entity.
     */
    abstract NettyEntityLocatable<?,?,?,?> trackEntitySpawn(PlayerData playerData, P packet, UUID world, int currentTick, EntityLocatable.SpawnType spawnType);

    /**   @return The entity ID of the entity   */
    protected abstract int processRelativeMovePacket(P packet, PlayerData playerData, int currentTick);

    /**   @return The entity ID of the entity   */
    protected abstract int processRelativeMoveAndRotationPacket(P packet, PlayerData playerData, int currentTick);

    /**   @return The entity ID of the entity   */
    protected abstract int processTeleportPacket(P packet, PlayerData playerData, int currentTick);

    /**   @return The entity ID of the entity   */
    protected abstract int processPositionSyncPacket(P packet, PlayerData playerData, int currentTick);

    protected abstract void cachePacket(P packet, int entityID, PlayerData playerData, int currentTick);

    /**   @return The entity ID of the entity   */
    protected abstract int processRotationPacket(P packet, PlayerData playerData, int currentTick);

    /**   @return The entity ID of the entity   */
    protected abstract int processHeadLookPacket(P packet, PlayerData playerData, int currentTick);

    /**   @return The entity ID of the entity   */
    protected abstract int processEntityVelocityPacket(P packet, PlayerData playerData, int currentTick);

    /**   @return The entity ID of the entity   */
    protected abstract int processEntityPassengersPacket(P packet, PlayerData playerData, int currentTick);

    protected abstract void processDestroyEntitiesPacket(P packet, PlayerData playerData, int currentTick);

    protected abstract void insertEntityToPlayerView(NettyEntityLocatable<?,?,?,?> entity, PlayerData playerData);

    protected abstract void insertEntityToEntityView(NettyEntityLocatable<?,?,?,?> entity, PlayerData playerData);
}
