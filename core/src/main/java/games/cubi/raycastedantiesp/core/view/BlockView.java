package games.cubi.raycastedantiesp.core.view;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.locatables.minecraft.TileEntityLocatable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BlockView {
    boolean isBlockOccluding(BlockLocatable location);

    void upsertTileEntity(BlockLocatable location, int currentTick);

    void insertIfAbsent(BlockLocatable location);

    void removeTileEntity(BlockLocatable location);

    TileEntityLocatable<?> getTrackedTileEntity(BlockLocatable location);

    TileEntityLocatable<?> getTrackedTileEntity(ImmutableBlockLocatable location);

    boolean isVisible(BlockLocatable location, int currentTick);

    void setVisibility(BlockLocatable location, boolean visible, int currentTick);

    Collection<BlockLocatable> getKnownTileEntities();

    Collection<BlockLocatable> getNeedingRecheck(int recheckTicks, int currentTick);

    boolean hasPendingTransitions();

    List<BlockViewTransition> drainTransitions();

    void upsertBlock(UUID world, int x, int y, int z, boolean occluding, boolean tileEntity);

    void removeChunk(UUID world, int chunkX, int chunkZ);

    void replaceChunk(UUID world, int chunkX, int chunkY, int chunkZ, boolean[][][] occludingBlocks);

    void clear();

    default <T> T cast() {
        return (T) this;
    }

    interface Factory {
        BlockView createBlockView();
    }
}
