package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.locatables.block.BlockLocatable;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public interface TileEntityVisibilityChanger {
    void showTileEntityToPlayer(UUID player, BlockLocatable tileEntity, int currentTick);
    void hideTileEntityFromPlayer(UUID player, BlockLocatable tileEntity, int currentTick);
    default void setTileEntityVisibilityForPlayer(UUID player, BlockLocatable tileEntity, boolean visible, int currentTick) {
        if (visible) {
            showTileEntityToPlayer(player, tileEntity, currentTick);
        } else {
            hideTileEntityFromPlayer(player, tileEntity, currentTick);
        }
    }

    VisibilityChangeHandlers.TileEntityVisibilityChangerType getType();

    void processCache();
}
