package games.cubi.raycastedantiesp.core.view;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;

import java.util.Collection;
import java.util.List;

public interface TileEntityView {
    void upsertTileEntity(BlockLocatable location, int currentTick);

    void insertIfAbsent(BlockLocatable location);

    void removeTileEntity(BlockLocatable location);

    TrackedTileEntity<?> getTrackedTileEntity(BlockLocatable location);

    TrackedTileEntity<?> getTrackedTileEntity(ImmutableBlockLocatable location);

    boolean isVisible(BlockLocatable location, int currentTick);

    void setVisibility(BlockLocatable location, boolean visible, int currentTick);

    Collection<BlockLocatable> getKnownTileEntities();

    Collection<BlockLocatable> getNeedingRecheck(int recheckTicks, int currentTick);

    boolean hasPendingTransitions();

    List<TileEntityViewTransition> drainTransitions();

    interface Factory {
        TileEntityView createTileEntityView();
    }
}
