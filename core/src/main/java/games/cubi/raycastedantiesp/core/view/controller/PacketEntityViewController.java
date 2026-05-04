package games.cubi.raycastedantiesp.core.view.controller;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.raycast.EntityConfig;
import games.cubi.raycastedantiesp.core.config.raycast.PlayerConfig;
import games.cubi.raycastedantiesp.core.config.raycast.RaycastConfig;
import games.cubi.raycastedantiesp.core.locatables.NettyEntityLocatable;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.utils.Packet;
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

    protected void handlePlayPhaseLoginPacket(int entityID, UUID playerUUID, int currentTick) {
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(playerUUID);
        playerData.playerView().insertEntity(createSelfEntity(entityID, playerUUID).cast());
    }

    protected PlayerData handleLoginPhaseLoginPacket(UUID playerUUID, int currentTick) {
        return PlayerRegistry.getInstance().registerAndGetPlayer(playerUUID, currentTick);
    }

    protected abstract NettyEntityLocatable<?,?,?,?> createSelfEntity(int entityID, UUID playerUUID);

    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleLivingEntitySpawn(P packet, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            Logger.error(new RuntimeException("World null when handling spawn living entity packet, uuid=" + playerData.getPlayerUUID() + " tick=" + currentTick), 2, PacketEntityViewController.class);
            return true;
        }
        NettyEntityLocatable<?,?,?,?> entity = processLivingEntitySpawn(playerData, packet, world, currentTick);

        if (entityConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnEntityDistanceSquared) {
                entity.setVisible(false);
                entity.setClientVisible(false);
                insertEntityToEntityView(entity, playerData);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        insertEntityToEntityView(entity, playerData);
        return false;
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    @Packet(Packet.Packets.SPAWN_ENTITY)
    protected boolean handleEntitySpawn(P packet, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            Logger.error(new RuntimeException("World null when handling spawn entity packet, uuid=" + playerData.getPlayerUUID() + " tick=" + currentTick), 2, PacketEntityViewController.class);
            return true;
        }

        NettyEntityLocatable<?,?,?,?> entity = processEntitySpawn(playerData, packet, world, currentTick);

        if (entityConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnEntityDistanceSquared) {
                entity.setVisible(false);
                entity.setClientVisible(false);
                insertEntityToEntityView(entity, playerData);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        insertEntityToEntityView(entity, playerData);
        return false;
    }

    @Packet(Packet.Packets.ENTITY_ANIMATION)
    protected boolean handleEntityAnimation(int entityID, PlayerData playerData) {
        return cancelIfEnabledAndHidden(entityID, playerData);
    }

    @Packet(Packet.Packets.ENTITY_EVENT)
    protected boolean handleEntityEvent(int entityID, PlayerData playerData) {
        return cancelIfEnabledAndHidden(entityID, playerData);
    }

    @Packet(Packet.Packets.HURT_ANIMATION)
    protected boolean handleHurtAnimation(int entityID, PlayerData playerData) {
        return cancelIfEnabledAndHidden(entityID, playerData);
    }

    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handlePaintingSpawn(P packet, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            Logger.error(new RuntimeException("World null when handling spawn painting packet, uuid=" + playerData.getPlayerUUID() + " tick=" + currentTick), 2, PacketEntityViewController.class);
            return true;
        }
        NettyEntityLocatable<?,?,?,?> entity = processPaintingSpawn(playerData, packet, world, currentTick);

        if (entityConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnEntityDistanceSquared) {
                entity.setVisible(false);
                entity.setClientVisible(false);
                insertEntityToEntityView(entity, playerData);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        insertEntityToEntityView(entity, playerData);
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
        NettyEntityLocatable<?,?,?,?> entity = processPlayerSpawn(playerData, packet, world, currentTick);

        if (playerConfig.enabled()) {
            double distanceSquared = playerData.ownLocation().distanceSquared(entity);
            if (distanceSquared > hideOnSpawnPlayerDistanceSquared) {
                entity.setVisible(false);
                entity.setClientVisible(false);
                insertEntityToPlayerView(entity, playerData);
                return true;
            }
        } else {
            entity.setClientVisible(true);
        }
        insertEntityToPlayerView(entity, playerData);
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
    protected boolean handleEntityMetadata(P packet, int entityID, PlayerData playerData, int currentTick) {
        cachePacket(packet, entityID, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }

    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleRemoveEntityEffect(P packet, int entityID, PlayerData playerData, int currentTick) {
        cachePacket(packet, entityID, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }

    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityEquipment(P packet, int entityID, PlayerData playerData, int currentTick) {
        cachePacket(packet, entityID, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityVelocity(P packet, int entityID, PlayerData playerData, int currentTick) {
        processEntityVelocityPacket(packet, playerData, currentTick);
        cachePacket(packet, entityID, playerData, currentTick); //todo: may be wrong?
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityEffect(P packet, int entityID, PlayerData playerData, int currentTick) {
        cachePacket(packet, entityID, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }
    /**
     * @return Whether or not to cancel the packet event. <code>true</code> to cancel, <code>false</code> to do nothing.
     */
    protected boolean handleEntityPassengers(P packet, PlayerData playerData, int currentTick) {
        int entityID = processEntityPassengersPacket(packet, playerData, currentTick);
        return cancelIfEnabledAndHidden(entityID, playerData);
    }

    protected void handleDestroyEntities(P packet, PlayerData playerData, int currentTick) {
        processDestroyEntitiesPacket(packet, playerData, currentTick);
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
        Logger.warning("Could not find view for entityID=" + entityID + " uuid=" + playerData.getPlayerUUID(), 4, PacketEntityViewController.class);
        return null;
    }

    protected NettyEntityLocatable<?,?,?,?> entityFromID(int entityID, PlayerData playerData) {
        EntityView<?> entityView = viewFromEntityID(entityID, playerData);
        if (entityView == null) {
            return null;
        }
        return (NettyEntityLocatable<?, ?, ?, ?>) entityView.getEntity(entityID);
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

        if (entityView == null) {
            Logger.error("Checked if packet for entity should be cancelled, but entity did not exist. ID: " + entityID + " for player: " + playerData.getPlayerUUID(), 2, PacketEntityViewController.class);
            return true;
        }

        if (entityView.isVisible(entityID)) {
            return false;
        }

        return getCorrectConfig(entityView).enabled(); // If this statement is reached, the entity should be hidden, so if the config is enabled it is hidden.
    }

    /**
     * @return The created entity, with a default visibility of <code>true</code>. Does not insert the entity into any views, that is the responsibility of the caller.
     */
    protected abstract NettyEntityLocatable<?,?,?,?> processLivingEntitySpawn(PlayerData playerData, P packet, UUID world, int currentTick);

    /**
     * @return The created entity, with a default visibility of <code>true</code>. Does not insert the entity into any views, that is the responsibility of the caller.
     */
    protected abstract NettyEntityLocatable<?,?,?,?> processEntitySpawn(PlayerData playerData, P packet, UUID world, int currentTick);

    /**
     * @return The created entity, with a default visibility of <code>true</code>. Does not insert the entity into any views, that is the responsibility of the caller.
     */
    protected abstract NettyEntityLocatable<?,?,?,?> processPaintingSpawn(PlayerData playerData, P packet, UUID world, int currentTick);

    /**
     * @return The created entity, with a default visibility of <code>true</code>. Does not insert the entity into any views, that is the responsibility of the caller.
     */
    protected abstract NettyEntityLocatable<?,?,?,?> processPlayerSpawn(PlayerData playerData, P packet, UUID world, int currentTick);

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
