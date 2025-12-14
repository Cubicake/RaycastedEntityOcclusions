package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.locatables.Locatable;

import java.util.Set;
import java.util.UUID;

public interface TileEntitySnapshotManager {
    Set<UUID> getPlayersWhoCanSee(Locatable tileEntity);
    int getTicksSincePlayerSawTileEntity(UUID player, Locatable tileEntity);
    Set<BlockLocation> getTileEntitiesInChunk(UUID world, int x, int z);
}
