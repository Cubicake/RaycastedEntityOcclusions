package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.locatables.Locatable;

import java.util.Set;
import java.util.UUID;

public interface TileEntitySnapshotManager {
    Set<UUID> getPlayersWhoCanSee(Locatable tileEntity);

    /**
     * Returns the number of ticks since the player last saw the tile entity.
     * @param player
     * @param tileEntity
     * @return -1 if the player cannot see the tile entity, otherwise the number of ticks since last seen.
     */
    int getTicksSincePlayerSawTileEntity(UUID player, Locatable tileEntity);
    Set<BlockLocation> getTileEntitiesInChunk(UUID world, int x, int z);
    SnapshotManager.TileEntitySnapshotManagerType getType();
}
