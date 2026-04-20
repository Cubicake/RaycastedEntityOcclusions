package games.cubi.raycastedantiesp.packetevents.viewcontrollers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.Locatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.view.BlockView;
import games.cubi.raycastedantiesp.core.view.BlockViewTransition;
import games.cubi.raycastedantiesp.core.locatables.TileEntityLocatable;
import games.cubi.raycastedantiesp.packetevents.BlockInfoResolver;
import games.cubi.raycastedantiesp.packetevents.replaydata.PacketEventsTileEntityReplayData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntSupplier;

public abstract class PacketEventsBlockViewController implements PacketListener {
    private final BlockInfoResolver blockInfoResolver;
    private final IntSupplier currentTickSupplier;
    private final PacketEventsCommonViewController common;

    protected PacketEventsBlockViewController(BlockInfoResolver blockInfoResolver, IntSupplier currentTickSupplier) {
        this.blockInfoResolver = blockInfoResolver;
        this.currentTickSupplier = currentTickSupplier;
        common = PacketEventsCommonViewController.get(currentTickSupplier);
    }

    protected abstract UUID resolveWorldUUID(User user);

    protected abstract int getHiddenBlockId(int blockY);

    public void removeViewer(UUID viewerUUID) {
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!blockInfoResolver.isInitialised()) {
            event.setCancelled(true);
            return;
        }
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

        handleBlockPackets(event, event.getUser(), playerData, world, currentTick);

        if (playerData.blockView().hasPendingTransitions()) {
            processTileEntityTransitions(event.getUser(), playerData.blockView());
        }
        event.getUser().flushPackets();
    }

    private void handleBlockPackets(PacketSendEvent event, User viewer, PlayerData playerData, UUID world, int currentTick) {
        if (world == null) {
            return;
        }

        BlockView blockView = playerData.blockView();

        if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
            WrapperPlayServerUnloadChunk packet = new WrapperPlayServerUnloadChunk(event);
            blockView.removeChunk(world, packet.getChunkX(), packet.getChunkZ());
            removeChunkTileEntities(blockView, world, packet.getChunkX(), packet.getChunkZ());
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);
            handleSingleBlockChange(event, viewer, playerData, world, packet, currentTick);
        } else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
            WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(event);
            for (WrapperPlayServerMultiBlockChange.EncodedBlock change : packet.getBlocks()) {
                int blockID = change.getBlockId();
                boolean occluding = blockID != 0 && blockInfoResolver.isOccluding(blockID);
                boolean tileEntity = blockInfoResolver.isTileEntity(blockID);
                blockView.upsertBlock(world, change.getX(), change.getY(), change.getZ(), occluding);
                ImmutableBlockLocatable location = new ImmutableBlockLocatable(world, change.getX(), change.getY(), change.getZ());
                if (tileEntity) {
                    blockView.insertTileEntityIfAbsent(location, blockID);
                    if (!blockView.isVisible(location, currentTick)) {
                        change.setBlockId(getHiddenBlockId(location.blockY()));
                        event.markForReEncode(true);
                    }
                } else {
                    blockView.removeTileEntity(location);
                }
            }
        } else if (event.getPacketType() == PacketType.Play.Server.BLOCK_ENTITY_DATA) {
            WrapperPlayServerBlockEntityData packet = new WrapperPlayServerBlockEntityData(event);
            ImmutableBlockLocatable location = new ImmutableBlockLocatable(world, packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ());
            TileEntityLocatable<PacketEventsTileEntityReplayData> tileEntity = getTrackedTileEntity(blockView, location);
            if (tileEntity == null) {
                Logger.warning("Received standalone block entity data for an uncached tile entity. Location: " + location.world() + " " + location.blockX() + "," + location.blockY() + "," + location.blockZ(), 3, PacketEventsBlockViewController.class);
                return;
            }
            ensureTileReplayData(tileEntity).setBlockEntityData(packet.getBlockEntityType(), packet.getNBT());
            if (!blockView.isVisible(location, currentTick)) {
                event.setCancelled(true);
                sendHiddenBlock(viewer, location);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
            WrapperPlayServerChunkData packet = new WrapperPlayServerChunkData(event);
            Column column = ingestChunk(
                    playerData,
                    world,
                    packet.getColumn().getX(),
                    packet.getColumn().getZ(),
                    packet.getColumn(),
                    event.getUser().getMinWorldHeight() >> 4,
                    event
            );
            Column tileEntitiesRemoved;
            if (column.hasBiomeData()) {
                int[] biomeInts = column.getBiomeDataInts();
                byte[] biomeBytes = column.getBiomeDataBytes();
                if (biomeInts.length >= biomeBytes.length) {
                    tileEntitiesRemoved = new Column(column.getX(), column.getZ(), column.isFullChunk(), column.getChunks(), new TileEntity[0], column.getHeightMaps(), biomeInts);
                }
                else {
                    tileEntitiesRemoved = new Column(column.getX(), column.getZ(), column.isFullChunk(), column.getChunks(), new TileEntity[0], column.getHeightMaps(), biomeBytes);
                }
            }
            else {
                if (common.v_1_21_5_orAbove) tileEntitiesRemoved = new Column(column.getX(), column.getZ(), column.isFullChunk(), column.getChunks(), new TileEntity[0], column.getHeightmaps());
                else tileEntitiesRemoved = new Column(column.getX(), column.getZ(), column.isFullChunk(), column.getChunks(), new TileEntity[0], column.getHeightMaps());
            }
            packet.setColumn(tileEntitiesRemoved);
            event.markForReEncode(true);

        } else if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
            WrapperPlayServerChunkDataBulk packet = new WrapperPlayServerChunkDataBulk(event);/*
            for (int i = 0; i < packet.getChunks().length; i++) {
                packet.
                Column strippedColumn = ingestChunk(
                        playerData,
                        world,
                        packet.getX()[i],
                        packet.getZ()[i],
                        packet.getChunks()[i],
                        null,
                        event.getUser().getMinWorldHeight() >> 4,
                        currentTick
                );
                reapplyHiddenChunkCovers(viewer, playerData, world, packet.getX()[i], packet.getZ()[i], currentTick);
            }*/
            throw new RuntimeException("I didn't think this packet existed. Please report this to the developer with details on how to reproduce it so it can be implemented");
        }
    }

    private void processTileEntityTransitions(User viewer, BlockView blockView) {
        for (BlockViewTransition transition : blockView.drainTransitions()) {
            BlockLocatable location = transition.location();
            TileEntityLocatable<PacketEventsTileEntityReplayData> state = getTrackedTileEntity(blockView, location);

            switch (transition.type()) {
                case HIDE -> viewer.writePacketSilently(new WrapperPlayServerBlockChange(
                        new Vector3i(location.blockX(), location.blockY(), location.blockZ()),
                        getHiddenBlockId(location.blockY())
                ));
                case SHOW -> {
                    if (state == null || state.blockID() == 0) {
                        continue;
                    }
                    viewer.writePacketSilently(new WrapperPlayServerBlockChange(
                            new Vector3i(location.blockX(), location.blockY(), location.blockZ()),
                            state.blockID()
                    ));
                    PacketEventsTileEntityReplayData replayData = ensureTileReplayData(state);
                    if (replayData.blockEntityType() != null && replayData.nbt() != null) {
                        viewer.writePacketSilently(buildBlockEntityDataPacket(location, replayData));
                    }
                }
            }
        }
    }

    private void handleSingleBlockChange(PacketSendEvent event, User viewer, PlayerData playerData, UUID world, WrapperPlayServerBlockChange packet, int currentTick) {
        int blockID = packet.getBlockId();
        boolean occluding = blockID != 0 && blockInfoResolver.isOccluding(blockID);
        boolean tileEntity = blockInfoResolver.isTileEntity(blockID);
        Vector3i position = packet.getBlockPosition();
        ImmutableBlockLocatable location = new ImmutableBlockLocatable(world, position.getX(), position.getY(), position.getZ());

        playerData.blockView().upsertBlock(world, position.getX(), position.getY(), position.getZ(), occluding);
        if (tileEntity) {
            playerData.blockView().insertTileEntityIfAbsent(location, blockID);
            if (!playerData.blockView().isVisible(location, currentTick)) {
                event.setCancelled(true);
                sendHiddenBlock(viewer, location);
            }
        } else {
            playerData.blockView().removeTileEntity(location);
        }
    }

    private Column ingestChunk(
            PlayerData playerData,
            UUID worldID,
            int chunkX,
            int chunkZ,
            Column column,
            int minimumChunkSectionY,
            PacketSendEvent event
    ) {
        Map<Integer, boolean[][][]> occludingBySectionY = new HashMap<>();
        Map<Integer, Set<ImmutableBlockLocatable>> tileEntitiesBySectionY = new HashMap<>();
        BlockView blockView = playerData.blockView();

        BaseChunk[] sections = column.getChunks();
        TileEntity[] chunkTileEntitiesData = column.getTileEntities();

        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            BaseChunk section = sections[sectionIndex];
            if (section == null) {
                continue;
            }

            int sectionY = minimumChunkSectionY + sectionIndex;
            boolean[][][] occluding = new boolean[16][16][16];
            Set<ImmutableBlockLocatable> tileEntities = tileEntitiesBySectionY.computeIfAbsent(sectionY, ignored -> new HashSet<>());

            boolean chunkSectionHasOccluding = false;

            for (int localX = 0; localX < 16; localX++) {
                for (int localY = 0; localY < 16; localY++) {
                    for (int localZ = 0; localZ < 16; localZ++) {
                        int blockID = section.getBlockId(localX, localY, localZ);
                        if (blockID == 0) {
                            continue;
                        }

                        int blockX = (chunkX << 4) + localX;
                        int blockY = (sectionY << 4) + localY;
                        int blockZ = (chunkZ << 4) + localZ;

                        if (blockInfoResolver.isOccluding(blockID)) {
                            occluding[localX][localY][localZ] = true;
                            chunkSectionHasOccluding = true;
                        }
                        if (blockInfoResolver.isTileEntity(blockID)) {
                            ImmutableBlockLocatable location = new ImmutableBlockLocatable(worldID, blockX, blockY, blockZ);
                            tileEntities.add(location);
                            blockView.insertTileEntityIfAbsent(location, blockID);
                            section.set(localX, localY, localZ, getHiddenBlockId(blockY));
                        }
                    }
                }
            }
            if (chunkSectionHasOccluding) {
                // skip empty sections to save memory
                occludingBySectionY.put(sectionY, occluding);
            }
        }

        if (chunkTileEntitiesData != null) {
            for (TileEntity tileEntity : chunkTileEntitiesData) {
                int blockX = (chunkX << 4) + tileEntity.getX();
                int blockY = tileEntity.getY();
                int blockZ = (chunkZ << 4) + tileEntity.getZ();
                int sectionY = blockY >> 4;
                ImmutableBlockLocatable location = new ImmutableBlockLocatable(worldID, blockX, blockY, blockZ);
                tileEntitiesBySectionY.computeIfAbsent(sectionY, ignored -> new HashSet<>()).add(location);

                TileEntityLocatable<PacketEventsTileEntityReplayData> state = getTrackedTileEntity(blockView, location);

                if (state == null) {
                    Logger.warning("Received block entity data for a tile entity that wasn't in the chunk's tile entity list. Location: " + location.world() + " " + location.blockX() + "," + location.blockY() + "," + location.blockZ(), 3, PacketEventsBlockViewController.class);
                    int sectionIndex = sectionY - minimumChunkSectionY;
                    if (sectionIndex < 0 || sectionIndex >= sections.length) {
                        Logger.warning("Skipping uncached chunk block entity with out-of-bounds section index. Location: " + location.world() + " " + location.blockX() + "," + location.blockY() + "," + location.blockZ(), 3, PacketEventsBlockViewController.class);
                        continue;
                    }

                    BaseChunk sourceSection = sections[sectionIndex];
                    if (sourceSection == null) {
                        Logger.warning("Skipping uncached chunk block entity because its section data is missing. Location: " + location.world() + " " + location.blockX() + "," + location.blockY() + "," + location.blockZ(), 3, PacketEventsBlockViewController.class);
                        continue;
                    }

                    int blockID = sourceSection.getBlockId(tileEntity.getX(), blockY & 15, tileEntity.getZ());
                    if (blockID <= 0) {
                        Logger.warning("Skipping uncached chunk block entity because the recovered block state was air or invalid (" + blockID + "). Location: " + location.world() + " " + location.blockX() + "," + location.blockY() + "," + location.blockZ(), 3, PacketEventsBlockViewController.class);
                        continue;
                    }

                    if (!blockInfoResolver.isTileEntity(blockID)) {
                        Logger.warning("Recovered uncached chunk block entity from chunk sections with a non-tile-entity block state ID (" + blockID + "). Location: " + location.world() + " " + location.blockX() + "," + location.blockY() + "," + location.blockZ(), 3, PacketEventsBlockViewController.class);
                    }

                    blockView.insertTileEntityIfAbsent(location, blockID);
                    state = getTrackedTileEntity(blockView, location);
                    if (state == null) {
                        Logger.warning("Skipping uncached chunk block entity because caching recovery failed. Location: " + location.world() + " " + location.blockX() + "," + location.blockY() + "," + location.blockZ(), 3, PacketEventsBlockViewController.class);
                        continue;
                    }
                }
                ensureTileReplayData(state).setBlockEntityData(packetTileEntityType(tileEntity), tileEntity.getNBT());
            }
        }

        Set<Integer> sectionYs = new HashSet<>(tileEntitiesBySectionY.keySet());
        sectionYs.addAll(occludingBySectionY.keySet());
        for (int sectionY : sectionYs) {
            boolean[][][] occluding = occludingBySectionY.get(sectionY);
            if (occluding != null) {
                blockView.replaceChunk(worldID, chunkX, sectionY, chunkZ, occluding);
            }
            //reconcileSectionTileEntities(blockView, worldID, chunkX, sectionY, chunkZ, tileEntitiesBySectionY.getOrDefault(sectionY, Set.of()));
        }

        return column;
    }
/*
    private void reconcileSectionTileEntities(BlockView blockView, UUID worldID, int chunkX, int chunkY, int chunkZ, Set<ImmutableBlockLocatable> sectionTileEntities) {
        for (BlockLocatable known : blockView.getKnownTileEntities()) {
            if (!sameChunkSection(known, worldID, chunkX, chunkY, chunkZ)) {
                continue;
            }
            ImmutableBlockLocatable immutable = new ImmutableBlockLocatable(known.world(), known.blockX(), known.blockY(), known.blockZ());
            if (!sectionTileEntities.contains(immutable)) {
                blockView.removeTileEntity(known);
            }
        }
    }*/

    private void removeChunkTileEntities(BlockView blockView, UUID worldID, int chunkX, int chunkZ) {
        for (BlockLocatable known : blockView.getKnownTileEntities()) {
            if (!sameChunk(known, worldID, chunkX, chunkZ)) {
                continue;
            }
            blockView.removeTileEntity(known);
        }
    }
/*
    private void reapplyHiddenChunkCovers(User viewer, PlayerData playerData, UUID worldID, int chunkX, int chunkZ, int currentTick) {
        for (BlockLocatable known : playerData.tileEntityView().getKnownTileEntities()) {
            if (!sameChunk(known, worldID, chunkX, chunkZ)) {
                continue;
            }
            if (!playerData.tileEntityView().isVisible(known, currentTick)) {
                sendHiddenBlock(viewer, known);
            }
        }
    }*/
    private void sendHiddenBlock(User viewer, BlockLocatable location) {
        viewer.writePacketSilently(new WrapperPlayServerBlockChange(
                new Vector3i(location.blockX(), location.blockY(), location.blockZ()),
                getHiddenBlockId(location.blockY())
        ));
    }

    private boolean sameChunk(BlockLocatable location, UUID worldID, int chunkX, int chunkZ) {
        return location.world().equals(worldID) && location.chunkX() == chunkX && location.chunkZ() == chunkZ;
    }

    private boolean sameChunkSection(BlockLocatable location, UUID worldID, int chunkX, int chunkY, int chunkZ) {
        return sameChunk(location, worldID, chunkX, chunkZ) && location.chunkY() == chunkY;
    }

    private WrapperPlayServerBlockEntityData copyBlockEntityDataPacket(WrapperPlayServerBlockEntityData packet) {
        return new WrapperPlayServerBlockEntityData(
                copyBlockVector(packet.getPosition()),
                packet.getBlockEntityType(),
                packet.getNBT()
        );
    }

    private WrapperPlayServerBlockEntityData buildBlockEntityDataPacket(BlockLocatable location, PacketEventsTileEntityReplayData replayData) {
        return new WrapperPlayServerBlockEntityData(
                new Vector3i(location.blockX(), location.blockY(), location.blockZ()),
                replayData.blockEntityType(),
                replayData.nbt()
        );
    }

    private BlockEntityType packetTileEntityType(TileEntity tileEntity) {
        return BlockEntityTypes.getById(
                PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(),
                tileEntity.getType()
        );
    }

    private Vector3i copyBlockVector(Vector3i vector) {
        return new Vector3i(vector.getX(), vector.getY(), vector.getZ());
    }

    @SuppressWarnings("unchecked")
    private TileEntityLocatable<PacketEventsTileEntityReplayData> getTrackedTileEntity(BlockView blockView, BlockLocatable location) {
        return (TileEntityLocatable<PacketEventsTileEntityReplayData>) blockView.getTrackedTileEntity(location);
    }

    private PacketEventsTileEntityReplayData ensureTileReplayData(TileEntityLocatable<PacketEventsTileEntityReplayData> tileEntity) {
        PacketEventsTileEntityReplayData replayData = tileEntity.extraData();
        if (replayData == null) {
            replayData = new PacketEventsTileEntityReplayData();
            tileEntity.setExtraData(replayData);
        }
        return replayData;
    }
}
