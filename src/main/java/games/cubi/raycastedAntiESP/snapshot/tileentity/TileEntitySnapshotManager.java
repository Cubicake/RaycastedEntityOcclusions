package games.cubi.raycastedAntiESP.snapshot.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import net.kyori.adventure.util.TriState;

import java.util.Set;
import java.util.UUID;

public interface TileEntitySnapshotManager {
    //Set<UUID> getPlayersWhoCanSee(Locatable tileEntity); why is this here? Is it needed?

    /**
     * Returns the number of ticks since the player last saw the tile entity.
     * @param player
     * @param tileEntity
     * @return -1 if the player cannot see the tile entity, otherwise the number of ticks since last seen.
     */
    int getTicksSincePlayerSawTileEntity(UUID player, AbstractBlockLocation tileEntity);
    Set<BlockLocation> getTileEntitiesInChunk(UUID world, int x, int z);
    SnapshotManager.TileEntitySnapshotManagerType getType();

    //
    // Methods implemented by PlayerLastSeenTracker:
    //

    Set<PlayerLastCheckTimestamp> getPlayerLastSeenTimestamps(AbstractBlockLocation location);

    boolean tileEntityLastSeenMapContains(AbstractBlockLocation location);

    void setValuesInTileEntityLastSeenMap(BlockLocation location, Set<PlayerLastCheckTimestamp> values);

    void removeFromTileEntityLastSeenMap(AbstractBlockLocation location);

    /**
     * @return TriState.TRUE if visible, TriState.FALSE if not visible, TriState.UNDEFINED if it has never been checked.
     */
    TriState isTileEntityVisibleToPlayer(BlockLocation location, UUID playerUUID);

    void addOrUpdateTileEntityLastSeenMap(BlockLocation location, UUID playerUUID, boolean visible);

    void clearTileEntityLastSeenMap();
}
