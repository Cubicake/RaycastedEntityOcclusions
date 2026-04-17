package games.cubi.raycastedantiesp.packetevents.viewcontrollers;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.teleport.RelativeFlag;
import com.github.retrooper.packetevents.protocol.world.Direction;
import com.github.retrooper.packetevents.protocol.world.PaintingType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityPositionSync;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPainting;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import games.cubi.locatables.minecraft.EntityLocatable;
import games.cubi.locatables.Locatable;
import games.cubi.locatables.minecraft.NettyEntityLocatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.view.EntityView;
import games.cubi.raycastedantiesp.core.view.EntityViewTransition;
import games.cubi.raycastedantiesp.packetevents.locatables.PacketEventsEntity;
import games.cubi.raycastedantiesp.packetevents.replaydata.PacketEventsEntityReplayData;
import games.cubi.raycastedantiesp.packetevents.replaydata.PacketEventsPlayerReplayData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.IntSupplier;

public abstract class PacketEventsEntityViewController implements PacketListener {
    private final IntSupplier currentTickSupplier;
    private final PacketEventsCommonViewController common;

    protected PacketEventsEntityViewController(IntSupplier currentTickSupplier) {
        this.currentTickSupplier = currentTickSupplier;
        common = PacketEventsCommonViewController.get(currentTickSupplier);
    }

    protected abstract UUID resolveWorldUUID(User user);

    protected abstract int getHiddenBlockId(int blockY);

    public void removeViewer(UUID viewerUUID) {
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        UUID viewerUUID = event.getUser().getUUID();
        if (viewerUUID == null) {
            return;
        }

        PlayerData playerData = common.ensurePlayerData(viewerUUID, event);
        if (playerData == null) {
            return;
        }

        Locatable ownLocation = playerData.ownLocation();
        UUID world = ownLocation != null ? ownLocation.world() : resolveWorldUUID(event.getUser());
        int currentTick = currentTickSupplier.getAsInt();

        handlePlayerInfoPackets(event, event.getUser(), playerData, world, currentTick);
        handleEntityPackets(event, event.getUser(), playerData, world, currentTick);

        if (playerData.entityView().hasPendingTransitions()) {
            processEntityTransitions(viewerUUID, event.getUser(), cast(playerData.entityView()), false);
        }

        if (playerData.playerView().hasPendingTransitions()) {
            processEntityTransitions(viewerUUID, event.getUser(), cast(playerData.playerView()), true);
        }
        
        event.getUser().flushPackets();
    }

    private void handlePlayerInfoPackets(PacketSendEvent event, User viewer, PlayerData playerData, UUID world, int currentTick) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(event);
            for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo entry : packet.getEntries()) {
                UUID targetUUID = entry.getProfileId();
                if (targetUUID == null) {
                    continue;
                }
                PacketEventsEntity entity = cast(playerData.playerView().getEntity(targetUUID));
                if (entity == null) {
                    continue;
                }
                ensurePlayerReplayData(entity).addPlayerInfoUpdate(new WrapperPlayServerPlayerInfoUpdate(packet.getActions(), List.of(entry)));
            }
        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            WrapperPlayServerPlayerInfoRemove packet = new WrapperPlayServerPlayerInfoRemove(event);
            List<UUID> remaining = new ArrayList<>();
            for (UUID targetUUID : packet.getProfileIds()) {
                if (playerData.playerView().isVisible(targetUUID, currentTick)) {
                    remaining.add(targetUUID);
                }
            }

            if (remaining.size() == packet.getProfileIds().size()) {
                return;
            }

            event.setCancelled(true);
            if (!remaining.isEmpty()) {
                viewer.sendPacketSilently(new WrapperPlayServerPlayerInfoRemove(remaining));
            }
        }
    }

    private void handleEntityPackets(PacketSendEvent event, User viewer, PlayerData playerData, UUID world, int currentTick) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            if (world == null) {
                Logger.error(new RuntimeException("World null when handling spawn living entity packet, uuid=" + event.getUser().getUUID() + " tick=" + currentTick), 2, PacketEventsEntityViewController.class);
                return;
            }
            WrapperPlayServerSpawnLivingEntity packet = new WrapperPlayServerSpawnLivingEntity(event);
            PacketEventsEntity entity = trackEntitySpawn(playerData.entityView().cast(), packet.getEntityUUID(), packet.getEntityId(), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ(), currentTick, EntityLocatable.SpawnType.LIVING, packet.getEntityType());
            if (entity == null) {
                Logger.error(new RuntimeException("Entity null after attempting to track entity spawn for living entity packet, uuid=" + packet.getEntityUUID() + " id=" + packet.getEntityId() + " tick=" + currentTick), 2, PacketEventsEntityViewController.class);;
                return;
            }
            entity
                    .setYaw(packet.getYaw())
                    .setPitch(packet.getPitch())
                    .setHeadYaw(packet.getHeadPitch())
                    .setVelocity(packet.getVelocity().getX(), packet.getVelocity().getY(), packet.getVelocity().getZ())
                    .setMetadata(copyEntityMetadata(packet.getEntityMetadata()))
                    .setOnGround(true);
            if (!entity.visible()) {
                entity.setClientVisible(false);
                event.setCancelled(true);
            } else {
                entity.setClientVisible(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            if (world == null) {
                return;
            }
            WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(event);
            if (packet.getUUID().isEmpty()) {
                Logger.error(new RuntimeException("Entity UUID null when handling spawn entity packet, id=" + packet.getEntityId() + " tick=" + currentTick), 2, PacketEventsEntityViewController.class);
                return;
            }
            UUID entityUUID = packet.getUUID().get();

            PacketEventsEntity entity = trackEntitySpawn(playerData.entityView().cast(), entityUUID, packet.getEntityId(), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ(), currentTick, EntityLocatable.SpawnType.ENTITY, packet.getEntityType());
            if (entity == null) {
                return;
            }
            Vector3d velocity = packet.getVelocity().orElseGet(Vector3d::zero);
            entity.setEntityData(packet.getData())
                    .setYaw(packet.getYaw())
                    .setPitch(packet.getPitch())
                    .setHeadYaw(packet.getHeadYaw())
                    .setVelocity(velocity.getX(), velocity.getY(), velocity.getZ())
                    .setOnGround(true);
            if (!entity.visible()) {
                entity.setClientVisible(false);
                event.setCancelled(true);
            } else {
                entity.setClientVisible(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PAINTING) {
            if (world == null) {
                return;
            }
            WrapperPlayServerSpawnPainting packet = new WrapperPlayServerSpawnPainting(event);
            PacketEventsEntity entity = trackEntitySpawn(playerData.entityView().cast(), packet.getUUID(), packet.getEntityId(), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ(), currentTick, EntityLocatable.SpawnType.PAINTING, packet.getType().orElse(null), packet.getDirection());
            if (entity == null) {
                return;
            }
            entity.setYaw(0.0F)
                    .setPitch(0.0F)
                    .setHeadYaw(0.0F)
                    .setVelocity(0.0, 0.0, 0.0)
                    .setOnGround(true);
            if (!entity.visible()) {
                entity.setClientVisible(false);
                event.setCancelled(true);
            } else {
                entity.setClientVisible(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            if (world == null) {
                return;
            }
            WrapperPlayServerSpawnPlayer packet = new WrapperPlayServerSpawnPlayer(event);
            PacketEventsEntity entity = trackEntitySpawn(playerData.playerView().cast(), packet.getUUID(), packet.getEntityId(), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ(), currentTick, EntityLocatable.SpawnType.PLAYER, null);
            if (entity == null) {
                return;
            }
            entity.setYaw(packet.getYaw())
                    .setPitch(packet.getPitch())
                    .setHeadYaw(packet.getYaw())
                    .setVelocity(0.0, 0.0, 0.0)
                    .setMetadata(copyEntityMetadata(packet.getEntityMetadata()))
                    .setOnGround(true);
            if (!entity.visible()) {
                entity.setClientVisible(false);
                event.setCancelled(true);
            } else {
                entity.setClientVisible(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            WrapperPlayServerEntityRelativeMove packet = new WrapperPlayServerEntityRelativeMove(event);
            handleRelativeMove(event, playerData, packet.getEntityId(), packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ(), packet.isOnGround(), currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            WrapperPlayServerEntityRelativeMoveAndRotation packet = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
            handleRelativeMoveAndRotation(
                    event,
                    playerData,
                    packet.getEntityId(),
                    packet.getDeltaX(),
                    packet.getDeltaY(),
                    packet.getDeltaZ(),
                    packet.getYaw(),
                    packet.getPitch(),
                    packet.isOnGround(),
                    currentTick
            );
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(event);
            handleTeleport(event, playerData, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_POSITION_SYNC) {
            WrapperPlayServerEntityPositionSync packet = new WrapperPlayServerEntityPositionSync(event);
            handlePositionSync(event, playerData, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) {
            WrapperPlayServerEntityRotation packet = new WrapperPlayServerEntityRotation(event);
            handleRotation(event, playerData, packet.getEntityId(), packet.getYaw(), packet.getPitch(), packet.isOnGround(), currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_HEAD_LOOK) {
            WrapperPlayServerEntityHeadLook packet = new WrapperPlayServerEntityHeadLook(event);
            handleHeadLook(event, playerData, packet.getEntityId(), packet.getHeadYaw(), currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
            handleEntityMetadata(event, playerData, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
            handleEntityEquipment(event, playerData, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity(event);
            handleEntityVelocity(event, playerData, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT) {
            WrapperPlayServerEntityEffect packet = new WrapperPlayServerEntityEffect(event);
            handleEntityEffect(event, playerData, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.SET_PASSENGERS) {
            WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
            handleEntityPassengers(event, playerData, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(event);
            handleDestroyEntities(event, viewer, playerData, packet, currentTick);
        }
    }

    private PacketEventsEntity trackEntitySpawn(
            EntityView<PacketEventsEntity> entityView,
            UUID entityUUID,
            int entityID,
            UUID world,
            double x,
            double y,
            double z,
            int currentTick,
            EntityLocatable.SpawnType spawnType,
            EntityType entityType
    ) {
        if (entityUUID == null) return null;

        PacketEventsEntity entity = new PacketEventsEntity(world, x, y, z, entityID, entityUUID, spawnType, entityType);
        entityView.insertEntity(entity);

        //entity.setLastChecked(currentTick); //test that this is safe to remove
        ensureReplayData(entity);
        return entity;
    }

    private PacketEventsEntity trackEntitySpawn(
            EntityView<PacketEventsEntity> entityView,
            UUID entityUUID,
            int entityID,
            UUID world,
            double x,
            double y,
            double z,
            int currentTick,
            EntityLocatable.SpawnType spawnType,
            PaintingType paintingType,
            Direction paintingDirection
    ) {
        if (entityUUID == null) return null;

        PacketEventsEntity entity = new PacketEventsEntity(world, x, y, z, entityID, entityUUID, spawnType, paintingType, paintingDirection);
        entityView.insertEntity(entity);

        //entity.setLastChecked(currentTick); //test that this is safe to remove
        ensureReplayData(entity);
        return entity;
    }

    private void handleRelativeMove(PacketSendEvent event, PlayerData playerData, int entityID, double deltaX, double deltaY, double deltaZ, boolean onGround, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, entityID);
        if (lookup == null) {
            return;
        }
        lookup.view().moveRelative(entityID, deltaX, deltaY, deltaZ, currentTick);
        lookup.entity().setOnGround(onGround);
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleRelativeMoveAndRotation(
            PacketSendEvent event,
            PlayerData playerData,
            int entityID,
            double deltaX,
            double deltaY,
            double deltaZ,
            float yaw,
            float pitch,
            boolean onGround,
            int currentTick
    ) {
        EntityLookup lookup = lookupEntity(playerData, entityID);
        if (lookup == null) {
            return;
        }
        lookup.view().moveRelative(entityID, deltaX, deltaY, deltaZ, currentTick);
        lookup.entity().setYaw(yaw).setPitch(pitch).setOnGround(onGround);
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleTeleport(PacketSendEvent event, PlayerData playerData, WrapperPlayServerEntityTeleport packet, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, packet.getEntityId());
        if (lookup == null) {
            return;
        }
        lookup.view().moveAbsolute(packet.getEntityId(), packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ(), currentTick);
        lookup.entity()
                .setYaw(packet.getYaw())
                .setPitch(packet.getPitch())
                .setVelocity(packet.getDeltaMovement().getX(), packet.getDeltaMovement().getY(), packet.getDeltaMovement().getZ())
                .setOnGround(packet.isOnGround());
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handlePositionSync(PacketSendEvent event, PlayerData playerData, WrapperPlayServerEntityPositionSync packet, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, packet.getId());
        if (lookup == null) {
            return;
        }
        lookup.view().moveAbsolute(
                packet.getId(),
                packet.getValues().getPosition().getX(),
                packet.getValues().getPosition().getY(),
                packet.getValues().getPosition().getZ(),
                currentTick
        );
        lookup.entity()
                .setYaw(packet.getValues().getYaw())
                .setPitch(packet.getValues().getPitch())
                .setVelocity(packet.getValues().getDeltaMovement().getX(), packet.getValues().getDeltaMovement().getY(), packet.getValues().getDeltaMovement().getZ())
                .setOnGround(packet.isOnGround());
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleRotation(PacketSendEvent event, PlayerData playerData, int entityID, float yaw, float pitch, boolean onGround, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, entityID);
        if (lookup == null) {
            return;
        }
        lookup.entity().setYaw(yaw).setPitch(pitch).setOnGround(onGround);
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleHeadLook(PacketSendEvent event, PlayerData playerData, int entityID, float headYaw, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, entityID);
        if (lookup == null) {
            return;
        }
        lookup.entity().setHeadYaw(headYaw);
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleEntityMetadata(PacketSendEvent event, PlayerData playerData, WrapperPlayServerEntityMetadata packet, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, packet.getEntityId());
        if (lookup == null) {
            return;
        }
        lookup.entity().setMetadata(copyEntityMetadata(packet.getEntityMetadata()));
        ensureReplayData(lookup.entity()).setMetadataPacket(copyEntityMetadataPacket(packet));
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleEntityEquipment(PacketSendEvent event, PlayerData playerData, WrapperPlayServerEntityEquipment packet, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, packet.getEntityId());
        if (lookup == null) {
            return;
        }
        lookup.entity().setEquipment(copyEquipment(packet.getEquipment()));
        ensureReplayData(lookup.entity()).setEquipmentPacket(copyEntityEquipmentPacket(packet));
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleEntityVelocity(PacketSendEvent event, PlayerData playerData, WrapperPlayServerEntityVelocity packet, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, packet.getEntityId());
        if (lookup == null) {
            return;
        }
        lookup.entity().setVelocity(packet.getVelocity().getX(), packet.getVelocity().getY(), packet.getVelocity().getZ());
        ensureReplayData(lookup.entity()).setVelocityPacket(copyEntityVelocityPacket(packet));
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleEntityEffect(PacketSendEvent event, PlayerData playerData, WrapperPlayServerEntityEffect packet, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, packet.getEntityId());
        if (lookup == null) {
            return;
        }
        ensureReplayData(lookup.entity()).addEffectPacket(copyEffectPacket(packet));
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleEntityPassengers(PacketSendEvent event, PlayerData playerData, WrapperPlayServerSetPassengers packet, int currentTick) {
        EntityLookup lookup = lookupEntity(playerData, packet.getEntityId());
        if (lookup == null) {
            return;
        }
        lookup.entity().setPassengerIDs(packet.getPassengers().clone());
        ensureReplayData(lookup.entity()).setPassengersPacket(copySetPassengersPacket(packet));
        if (!lookup.view().isVisible(lookup.entity().entityUUID(), currentTick)) {
            event.setCancelled(true);
        }
    }

    private void handleDestroyEntities(PacketSendEvent event, User viewer, PlayerData playerData, WrapperPlayServerDestroyEntities packet, int currentTick) {
        List<Integer> remaining = new ArrayList<>();
        for (int entityID : packet.getEntityIds()) {

            EntityLookup lookup = lookupEntity(playerData, entityID);
            if (lookup == null) {
                remaining.add(entityID);
                continue;
            }

            boolean visible = lookup.view().isVisible(lookup.entity().entityUUID(), currentTick);
            boolean clientVisible = lookup.entity().clientVisible();
            lookup.view().removeEntity(entityID, currentTick);

            if (visible && clientVisible) {
                remaining.add(entityID);
            }
        }

        if (remaining.size() == packet.getEntityIds().length) {
            return;
        }

        event.setCancelled(true);
        if (!remaining.isEmpty()) {
            viewer.sendPacketSilently(new WrapperPlayServerDestroyEntities(remaining.stream().mapToInt(Integer::intValue).toArray()));
        }
    }

    private void processEntityTransitions(UUID viewerUUID, User viewer, EntityView<PacketEventsEntity> entityView, boolean playerTargets) {
        for (EntityViewTransition transition : entityView.drainTransitions()) {
            PacketEventsEntity entity = getTrackedEntity(entityView, transition.targetUUID());

            switch (transition.type()) {
                case HIDE -> {
                    if (entity != null && entity.clientVisible() && entity.entityID() >= 0) {
                        viewer.writePacketSilently(new WrapperPlayServerDestroyEntities(entity.entityID()));
                        entity.setClientVisible(false);
                    }
                }
                case SHOW -> {
                    if (entity == null || entity.spawnType() == null) {
                        Logger.warning("PacketEvents.processEntityTransitions show-skipped viewer=" + viewerUUID
                                + " target=" + transition.targetUUID()
                                + " reason="
                                + (entity == null ? "missing-entity" : "missing-spawn-type"), 2, PacketEventsEntityViewController.class);
                        continue;
                    }
                    PacketEventsEntityReplayData replayData = ensureReplayData(entity);
                    sendEntityShow(viewer, entity, replayData, playerTargets);
                    entity.setClientVisible(true);
                }
                case FORGET -> {
                }
            }
        }
    }

    private EntityLookup lookupEntity(PlayerData playerData, int entityID) {
        PacketEventsEntity playerEntity = getTrackedEntity(cast(playerData.playerView()), entityID);
        if (playerEntity != null) {
            return new EntityLookup(playerEntity, playerData.playerView());
        }
        PacketEventsEntity entity = getTrackedEntity(cast(playerData.entityView()), entityID);
        if (entity != null) {
            return new EntityLookup(entity, playerData.entityView());
        }
        return null;
    }

    private PacketWrapper<?> buildSpawnPacket(PacketEventsEntity entity) {
        return switch (entity.spawnType()) {
            case LIVING -> {
                List<EntityData<?>> metadata = copyEntityMetadata(cast(entity.metadata()));
                yield new WrapperPlayServerSpawnLivingEntity(
                        entity.entityID(),
                        entity.entityUUID(),
                        cast(entity.entityType()),
                        new Vector3d(entity.x(), entity.y(), entity.z()),
                        entity.yaw(),
                        entity.pitch(),
                        entity.headYaw(),
                        new Vector3d(entity.velocityX(), entity.velocityY(), entity.velocityZ()),
                        metadata
                );
            }
            case ENTITY -> new WrapperPlayServerSpawnEntity(
                    entity.entityID(),
                    Optional.of(entity.entityUUID()),
                    cast(entity.entityType()),
                    new Vector3d(entity.x(), entity.y(), entity.z()),
                    entity.pitch(),
                    entity.yaw(),
                    entity.headYaw(),
                    entity.entityData(),
                    Optional.of(new Vector3d(entity.velocityX(), entity.velocityY(), entity.velocityZ()))
            );
            case PAINTING -> new WrapperPlayServerSpawnPainting(
                    entity.entityID(),
                    entity.entityUUID(),
                    cast(entity.paintingType()),
                    new Vector3i(entity.blockX(), entity.blockY(), entity.blockZ()),
                    cast(entity.paintingDirection())
            );
            case PLAYER -> {
                List<EntityData<?>> metadata = copyEntityMetadata(cast(entity.metadata()));
                yield new WrapperPlayServerSpawnPlayer(
                        entity.entityID(),
                        entity.entityUUID(),
                        new Vector3d(entity.x(), entity.y(), entity.z()),
                        entity.yaw(),
                        entity.pitch(),
                        metadata
                );
            }
        };
    }

    private WrapperPlayServerEntityMetadata buildMetadataPacket(PacketEventsEntity entity) {
        List<EntityData<?>> metadata = cast(entity.metadata());
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        return new WrapperPlayServerEntityMetadata(entity.entityID(), copyEntityMetadata(metadata));
    }

    private WrapperPlayServerEntityEquipment buildEquipmentPacket(PacketEventsEntity entity) {
        List<Equipment> equipment = cast(entity.equipment());
        if (equipment == null || equipment.isEmpty()) {
            return null;
        }
        return new WrapperPlayServerEntityEquipment(entity.entityID(), copyEquipment(equipment));
    }

    private WrapperPlayServerSetPassengers buildPassengersPacket(PacketEventsEntity entity) {
        int[] passengerIDs = entity.passengerIDs();
        if (passengerIDs.length == 0) {
            return null;
        }
        return new WrapperPlayServerSetPassengers(entity.entityID(), passengerIDs);
    }

    private WrapperPlayServerEntityVelocity buildVelocityPacket(PacketEventsEntity entity) {
        return new WrapperPlayServerEntityVelocity(
                entity.entityID(),
                new Vector3d(entity.velocityX(), entity.velocityY(), entity.velocityZ())
        );
    }

    private WrapperPlayServerEntityEffect copyEffectPacket(WrapperPlayServerEntityEffect effect) {
        WrapperPlayServerEntityEffect copy = new WrapperPlayServerEntityEffect(
                effect.getEntityId(),
                effect.getPotionType(),
                effect.getEffectAmplifier(),
                effect.getEffectDurationTicks(),
                buildEffectFlags(effect.isAmbient(), effect.isVisible(), effect.isShowIcon())
        );
        copy.setFactorData(effect.getFactorData());
        return copy;
    }

    private WrapperPlayServerEntityMetadata copyEntityMetadataPacket(WrapperPlayServerEntityMetadata packet) {
        return new WrapperPlayServerEntityMetadata(
                packet.getEntityId(),
                copyEntityMetadata(packet.getEntityMetadata())
        );
    }

    private WrapperPlayServerEntityEquipment copyEntityEquipmentPacket(WrapperPlayServerEntityEquipment packet) {
        return new WrapperPlayServerEntityEquipment(
                packet.getEntityId(),
                copyEquipment(packet.getEquipment())
        );
    }

    private WrapperPlayServerEntityVelocity copyEntityVelocityPacket(WrapperPlayServerEntityVelocity packet) {
        return new WrapperPlayServerEntityVelocity(
                packet.getEntityId(),
                new Vector3d(packet.getVelocity().getX(), packet.getVelocity().getY(), packet.getVelocity().getZ())
        );
    }

    private WrapperPlayServerSetPassengers copySetPassengersPacket(WrapperPlayServerSetPassengers packet) {
        return new WrapperPlayServerSetPassengers(
                packet.getEntityId(),
                packet.getPassengers().clone()
        );
    }

    private PacketWrapper<?> copySpawnPacket(PacketWrapper<?> packet) {
        if (packet instanceof WrapperPlayServerSpawnLivingEntity living) {
            return new WrapperPlayServerSpawnLivingEntity(
                    living.getEntityId(),
                    living.getEntityUUID(),
                    living.getEntityType(),
                    new Vector3d(living.getPosition().getX(), living.getPosition().getY(), living.getPosition().getZ()),
                    living.getYaw(),
                    living.getPitch(),
                    living.getHeadPitch(),
                    new Vector3d(living.getVelocity().getX(), living.getVelocity().getY(), living.getVelocity().getZ()),
                    copyEntityMetadata(living.getEntityMetadata())
            );
        }
        if (packet instanceof WrapperPlayServerSpawnEntity entity) {
            return new WrapperPlayServerSpawnEntity(
                    entity.getEntityId(),
                    entity.getUUID(),
                    entity.getEntityType(),
                    new Vector3d(entity.getPosition().getX(), entity.getPosition().getY(), entity.getPosition().getZ()),
                    entity.getPitch(),
                    entity.getYaw(),
                    entity.getHeadYaw(),
                    entity.getData(),
                    entity.getVelocity().map(vector -> new Vector3d(vector.getX(), vector.getY(), vector.getZ()))
            );
        }
        if (packet instanceof WrapperPlayServerSpawnPainting painting) {
            return new WrapperPlayServerSpawnPainting(
                    painting.getEntityId(),
                    painting.getUUID(),
                    painting.getType().orElse(null),
                    new Vector3i(painting.getPosition().getX(), painting.getPosition().getY(), painting.getPosition().getZ()),
                    painting.getDirection()
            );
        }
        if (packet instanceof WrapperPlayServerSpawnPlayer player) {
            return new WrapperPlayServerSpawnPlayer(
                    player.getEntityId(),
                    player.getUUID(),
                    new Vector3d(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()),
                    player.getYaw(),
                    player.getPitch(),
                    copyEntityMetadata(player.getEntityMetadata())
            );
        }
        throw new IllegalArgumentException("Unsupported spawn packet cache type: " + packet.getClass().getName());
    }

    private List<EntityData<?>> copyEntityMetadata(List<EntityData<?>> metadata) {
        return metadata == null ? List.of() : List.copyOf(metadata);
    }

    private List<Equipment> copyEquipment(List<Equipment> equipment) {
        return equipment == null ? List.of() : List.copyOf(equipment);
    }

    private byte buildEffectFlags(boolean ambient, boolean visible, boolean showIcon) {
        byte flags = 0;
        if (ambient) {
            flags |= 1;
        }
        if (visible) {
            flags |= 2;
        }
        if (showIcon) {
            flags |= 4;
        }
        return flags;
    }
    
    @SuppressWarnings("unchecked")
    public  <T> T cast(Object value) {
        return (T) value;
    }

    private PacketEventsEntity getTrackedEntity(EntityView<PacketEventsEntity> entityView, UUID entityUUID) {
        return entityView.getEntity(entityUUID);
    }

    private PacketEventsEntity getTrackedEntity(EntityView<PacketEventsEntity> entityView, int entityID) {
        return entityView.getEntity(entityID);
    }

    private PacketEventsEntityReplayData ensureReplayData(PacketEventsEntity entity) {
        PacketEventsEntityReplayData replayData = cast(entity.packetReplayData());
        if (replayData == null) {
            replayData = PacketEventsEntityReplayData.create();
            entity.setPacketReplayData(replayData);
        }
        return replayData;
    }

    private PacketEventsPlayerReplayData ensurePlayerReplayData(PacketEventsEntity entity) {
        PacketEventsPlayerReplayData replayData = cast(entity.packetReplayData());
        if (replayData == null) {
            replayData = PacketEventsPlayerReplayData.create();
            entity.setPacketReplayData(replayData);
        }
        return replayData;
    }

    private void sendEntityShow(User viewer, PacketEventsEntity entity, PacketEventsEntityReplayData replayData, boolean playerTargets) {
        if (playerTargets) {
            PacketEventsPlayerReplayData data = replayData.asPlayerReplayData();
            for (WrapperPlayServerPlayerInfoUpdate playerInfoUpdate : data.playerInfoUpdates()) {
                viewer.writePacketSilently(playerInfoUpdate);
            }
        }

        viewer.writePacket(buildSpawnPacket(entity));
        sendEntityAbsoluteCorrection(viewer, entity);
        common.writeIfPresent(viewer, replayData.metadataPacket() != null ? replayData.metadataPacket() : buildMetadataPacket(entity));
        common.writeIfPresent(viewer, replayData.equipmentPacket() != null ? replayData.equipmentPacket() : buildEquipmentPacket(entity));
        common.writeIfPresent(viewer, replayData.velocityPacket() != null ? replayData.velocityPacket() : buildVelocityPacket(entity));
        common.writeIfPresent(viewer, replayData.passengersPacket() != null ? replayData.passengersPacket() : buildPassengersPacket(entity));
        for (WrapperPlayServerEntityEffect effectPacket : replayData.effectPackets()) {
            viewer.writePacketSilently(effectPacket);
        }
    }

    private void sendEntityAbsoluteCorrection(User viewer, PacketEventsEntity entity) {
        if (entity.entityID() < 0) {
            return;
        }
        viewer.writePacketSilently(new WrapperPlayServerEntityTeleport(
                entity.entityID(),
                new Vector3d(entity.x(), entity.y(), entity.z()),
                new Vector3d(entity.velocityX(), entity.velocityY(), entity.velocityZ()),
                entity.yaw(),
                entity.pitch(),
                RelativeFlag.NONE,
                entity.onGround()
        ));
        viewer.writePacketSilently(new WrapperPlayServerEntityHeadLook(entity.entityID(), entity.headYaw()));
    }

    private record EntityLookup(
            PacketEventsEntity entity,
            EntityView<?> view
    ) {}
}
