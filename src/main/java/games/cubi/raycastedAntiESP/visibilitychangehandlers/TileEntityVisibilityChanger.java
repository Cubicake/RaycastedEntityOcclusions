package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import games.cubi.raycastedAntiESP.locatables.Locatable;

import java.util.UUID;

public interface TileEntityVisibilityChanger {
    void showTileEntityToPlayer(UUID player, Locatable tileEntity);
    void hideTileEntityFromPlayer(UUID player, Locatable tileEntity);
}
