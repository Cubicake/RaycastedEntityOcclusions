package games.cubi.raycastedAntiESP.snapshot.tileentity;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import net.kyori.adventure.util.TriState;

import java.util.Set;
import java.util.UUID;

import static games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity.BukkitTVC.firstCastOccurredA;

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
    // Methods implemented by PlayerLastSeenTracker: todo bad inheritance pattern
    //

    Set<PlayerLastCheckTimestamp> getPlayerLastSeenTimestamps(AbstractBlockLocation location);

    boolean tileEntityLastSeenMapContains(AbstractBlockLocation location);

    TriState canPlayerSeeTileEntity(UUID playerUUID, BlockLocation location);

    void setValuesInTileEntityLastSeenMap(BlockLocation location, Set<PlayerLastCheckTimestamp> values);

    /**
     * Removes the entry for all players for the given tile entity location. Should be called when a tile entity is removed, to prevent memory leaks. Should <b>NOT</b> be called when a tile entity is hidden from a player, as other players may still be able to see it.
     */
    void removeFromTileEntityLastSeenMap(AbstractBlockLocation location);

    void markAsNotVisible(BlockLocation location, UUID playerUUID);

    /**
     * @return TriState.TRUE if visible, TriState.FALSE if not visible, TriState.UNDEFINED if it has never been checked. Read only, does not update any values. For debugging internal state only
     */
    TriState isTileEntityVisibleToPlayer(BlockLocation location, UUID playerUUID);

    void addOrUpdateTileEntityLastSeenMap(BlockLocation location, UUID playerUUID, boolean visible);

    /**
     * @param visible the value to set for the player and tile entity location. true if the player can see the tile entity, false if they cannot.
     * @return TriState.TRUE if the value was changed to match the provided visible value, TriState.FALSE if the value was already the provided visible value, TriState.NOT_SET if the value had never been set before.
     */
    TriState compareAndSet(BlockLocation location, UUID playerUUID, boolean visible);

    /**
     * @return true if the value was changed to match the provided visible value including if it was not set before, false if the value was already the provided visible value
     */

    default boolean compareAndSetReturningBoolean(BlockLocation location, UUID playerUUID, boolean visible) {

        final boolean isFirstCast;

        if (firstCastOccurredA.compareAndSet(false, true)) {
            Logger.warning("Running first cast " + isTileEntityVisibleToPlayer(location, playerUUID), 3);
            isFirstCast = true;
        }
        else {
            isFirstCast = false;
        }

        TriState result = compareAndSet(location, playerUUID, visible);

        if (isFirstCast) {
            Logger.warning("First cast result: " + result, 3);
        }

        return result != TriState.FALSE;
    }

    void clearTileEntityLastSeenMap();
}
