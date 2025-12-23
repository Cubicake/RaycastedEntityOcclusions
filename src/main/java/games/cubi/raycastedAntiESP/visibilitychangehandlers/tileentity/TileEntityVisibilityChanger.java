package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public interface TileEntityVisibilityChanger {
    void showTileEntityToPlayer(UUID player, BlockLocation tileEntity);
    void hideTileEntityFromPlayer(UUID player, BlockLocation tileEntity);
    default void setTileEntityVisibilityForPlayer(UUID player, BlockLocation tileEntity, boolean visible) {
        if (visible) {
            showTileEntityToPlayer(player, tileEntity);
        } else {
            hideTileEntityFromPlayer(player, tileEntity);
        }
    }

    VisibilityChangeHandlers.TileEntityVisibilityChangerType getType();

    void processCache();
}
