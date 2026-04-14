package games.cubi.raycastedantiesp.paper.snapshot.block.packet;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import games.cubi.locatables.ChunkLocatable;
import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Deprecated(forRemoval = true)
public class PacketEventsBSM implements PlayerBlockSnapshotManager, PlayerBlockSnapshotManager.Factory {

    private final OcclusionStateStore occlusionStateStore;
    private final TileEntityStateStore<NBTCompound> tileEntityStateStore;

    public PacketEventsBSM() {
        this(new OcclusionStateStore(), null);
    }

    private PacketEventsBSM(OcclusionStateStore occlusionStateStore, TileEntityStateStore<NBTCompound> tileEntityStateStore) {
        this.occlusionStateStore = occlusionStateStore;
        this.tileEntityStateStore = tileEntityStateStore;
    }

    @Override
    public boolean isBlockOccluding(BlockLocatable location) {
        return occlusionStateStore.isOccluding(location);
    }

    @Override
    public Set<ImmutableBlockLocatable> getTileEntitiesInChunk(ChunkLocatable chunkLocatable) {
        if (tileEntityStateStore == null) {
            Logger.warning("No tile entity snapshot store is available for this PacketEventsBSM instance", 5, PacketEventsBSM.class);
            return Set.of();
        }
        Set<PacketTileEntity<NBTCompound>> tileEntities = tileEntityStateStore.getTileEntitiesInChunk(chunkLocatable.world(), chunkLocatable.chunkX(), chunkLocatable.chunkZ());
        return tileEntities.stream().map(PacketTileEntity::location).collect(Collectors.toSet());
    }

    @Override
    public Set<ImmutableBlockLocatable> getTileEntitiesInChunk(UUID world, int chunkX, int chunkZ) {
        return getTileEntitiesInChunk(new ChunkLocatable.ImmutableChunkLocatable(world, chunkX, chunkZ));
    }

    @Override
    public SnapshotManager.SnapshotManagerType getType() {
        return SnapshotManager.SnapshotManagerType.PACKETEVENTS;
    }

    @Override
    public PlayerBlockSnapshotManager createPlayerBlockSnapshotManager() {
        return new PacketEventsBSM();
    }
}
