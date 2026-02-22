package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public interface TileEntityVisibilityChanger {
    void showTileEntityToPlayer(UUID player, AbstractBlockLocation tileEntity, int currentTick);
    void hideTileEntityFromPlayer(UUID player, AbstractBlockLocation tileEntity, int currentTick);
    default void setTileEntityVisibilityForPlayer(UUID player, AbstractBlockLocation tileEntity, boolean visible, int currentTick) {
        if (visible) {
            showTileEntityToPlayer(player, tileEntity, currentTick);
        } else {
            hideTileEntityFromPlayer(player, tileEntity, currentTick);
        }
    }

    VisibilityChangeHandlers.TileEntityVisibilityChangerType getType();

    void processCache();
}
