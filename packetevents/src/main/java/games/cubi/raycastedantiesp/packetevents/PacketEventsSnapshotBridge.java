package games.cubi.raycastedantiesp.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkDataBulk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityPositionSync;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPainting;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;

import games.cubi.locatables.Locatable;
import games.cubi.raycastedantiesp.core.packets.core.PacketBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.PlayerEntitySnapshotManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// PacketEvents adapter that feeds backend-neutral snapshot managers.
public abstract class PacketEventsSnapshotBridge implements PlayerEntitySnapshotManager.Factory, PlayerBlockSnapshotManager.Factory, PacketListener {

    private final BlockInfoResolver blockOcclusionResolver;

    public PacketEventsSnapshotBridge(
            BlockInfoResolver blockInfoResolver
    ) {
        this.blockOcclusionResolver = blockInfoResolver;
        registerListener();
    }

    protected abstract void registerListener();

    protected abstract UUID resolveWorldUUID(User user);

    @Override
    public void onPacketSend(PacketSendEvent event) {
        UUID playerID = event.getUser().getUUID();
        if (playerID == null) {
            return;
        }
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(playerID);
        if (playerData == null) {
            if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
                PlayerRegistry.getInstance().registerPlayer(playerID, false, 0);
                playerData = PlayerRegistry.getInstance().getPlayerData(playerID);
            } else {
                return;
            }
        }
        final var entitySnapshotManager = playerData.entitySnapshotManager();
        final var blockSnapshotManager = playerData.blockSnapshotManager();
        if (entitySnapshotManager == null || blockSnapshotManager == null) {
            return;
        }

        Locatable location = entitySnapshotManager.getLocation(playerID);
        final UUID world = location != null ? location.world() : resolveWorldUUID(event.getUser());

        handleEntityPackets(event, entitySnapshotManager, world);
        handleBlockPackets(event, blockSnapshotManager, world, blockOcclusionResolver);
    }

    private void handleEntityPackets(PacketSendEvent event, PlayerEntitySnapshotManager entitySnapshotManager, UUID world) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            if (world == null) {
                return;
            }
            final WrapperPlayServerSpawnLivingEntity packet = new WrapperPlayServerSpawnLivingEntity(event);
            entitySnapshotManager.upsertEntity(packet.getEntityId(), packet.getEntityUUID(), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ());
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            if (world == null) {
                return;
            }
            final WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(event);
            entitySnapshotManager.upsertEntity(packet.getEntityId(), packet.getUUID().orElse(null), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ());
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PLAYER) {
            if (world == null) {
                return;
            }
            final WrapperPlayServerSpawnPlayer packet = new WrapperPlayServerSpawnPlayer(event);
            entitySnapshotManager.upsertEntity(packet.getEntityId(), packet.getUUID(), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ());
        } else if (event.getPacketType() == PacketType.Play.Server.SPAWN_PAINTING) {
            if (world == null) {
                return;
            }
            final WrapperPlayServerSpawnPainting packet = new WrapperPlayServerSpawnPainting(event);
            entitySnapshotManager.upsertEntity(packet.getEntityId(), packet.getUUID(), world,
                    packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ());
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            final WrapperPlayServerEntityRelativeMove packet = new WrapperPlayServerEntityRelativeMove(event);
            entitySnapshotManager.moveRelative(packet.getEntityId(), packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            final WrapperPlayServerEntityRelativeMoveAndRotation packet = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
            entitySnapshotManager.moveRelative(packet.getEntityId(), packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            final WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(event);
            entitySnapshotManager.moveAbsolute(packet.getEntityId(), packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ());
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_POSITION_SYNC) {
            final WrapperPlayServerEntityPositionSync packet = new WrapperPlayServerEntityPositionSync(event);
            entitySnapshotManager.moveAbsolute(packet.getId(),
                    packet.getValues().getPosition().getX(),
                    packet.getValues().getPosition().getY(),
                    packet.getValues().getPosition().getZ());
        } else if (event.getPacketType() == PacketType.Play.Server.DESTROY_ENTITIES) {
            final WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(event);
            for (int entityId : packet.getEntityIds()) {
                entitySnapshotManager.removeEntity(entityId);
            }
        }
    }

    private void handleBlockPackets(
            PacketSendEvent event,
            PlayerBlockSnapshotManager blockSnapshotManager,
            UUID world,
            BlockInfoResolver blockInfoResolver
    ) {
        if (world == null) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
            final WrapperPlayServerUnloadChunk packet = new WrapperPlayServerUnloadChunk(event);
            blockSnapshotManager.removeChunk(world, packet.getChunkX(), packet.getChunkZ());
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            final WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);
            final int blockId = packet.getBlockId();
            final boolean occluding = blockId != 0 && blockInfoResolver.isOccluding(blockId);
            final boolean tileEntity = blockInfoResolver.isTileEntity(blockId);
            blockSnapshotManager.upsertBlock(world,
                    packet.getBlockPosition().getX(),
                    packet.getBlockPosition().getY(),
                    packet.getBlockPosition().getZ(),
                    occluding,
                    tileEntity);
        } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            final WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(event);
            for (WrapperPlayServerMultiBlockChange.EncodedBlock change : packet.getBlocks()) {
                final int blockId = change.getBlockId();
                final boolean occluding = blockId != 0 && blockInfoResolver.isOccluding(blockId);
                final boolean tileEntity = blockInfoResolver.isTileEntity(blockId);
                blockSnapshotManager.upsertBlock(world, change.getX(), change.getY(), change.getZ(), occluding, tileEntity);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            final WrapperPlayServerChunkData chunkData = new WrapperPlayServerChunkData(event);
            ingestChunk(world, blockSnapshotManager, chunkData.getColumn().getX(), chunkData.getColumn().getZ(), chunkData.getColumn().getChunks(), blockInfoResolver, event.getUser().getMinWorldHeight() >> 4);
        } else if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
            final WrapperPlayServerChunkDataBulk chunkBulk = new WrapperPlayServerChunkDataBulk(event);
            for (int i = 0; i < chunkBulk.getChunks().length; i++) {
                ingestChunk(world, blockSnapshotManager, chunkBulk.getX()[i], chunkBulk.getZ()[i], chunkBulk.getChunks()[i], blockInfoResolver, event.getUser().getMinWorldHeight() >> 4);
            }
        }
    }

    private void ingestChunk(
            UUID worldId,
            PlayerBlockSnapshotManager blockSnapshotManager,
            int chunkX,
            int chunkZ,
            BaseChunk[] sections,
            BlockInfoResolver blockInfoResolver,
            int minimumChunkSectionY
    ) {
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            final BaseChunk section = sections[sectionIndex];
            if (section == null) {
                continue;
            }

            final Set<Short> occluding = new HashSet<>();
            final Set<Short> tileEntities = new HashSet<>();

            for (int localX = 0; localX < 16; localX++) {
                for (int localY = 0; localY < 16; localY++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        final int blockId = section.getBlockId(localX, localY, localZ);
                        if (blockId == 0) {
                            continue;
                        }

                        if (blockInfoResolver.isOccluding(blockId)) {
                            occluding.add(PacketBlockSnapshotManager.packBlock(localX, localY, localZ));
                        }
                        if (blockInfoResolver.isTileEntity(blockId)) {
                            tileEntities.add(PacketBlockSnapshotManager.packBlock(localX, localY, localZ));
                        }
                    }
                }
            }
            blockSnapshotManager.replaceChunk(worldId, chunkX, minimumChunkSectionY + sectionIndex, chunkZ, occluding, tileEntities);
        }
    }
}
