package games.cubi.raycastedAntiESP.snapshot.block;

import games.cubi.raycastedAntiESP.data.PlayerRegistry;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.utils.PlayerData;

import java.util.Set;
import java.util.UUID;

public interface BlockSnapshotManager {
    default boolean isBlockOccluding(AbstractBlockLocation location, UUID player) {
        return isBlockOccluding(location, PlayerRegistry.getInstance().getPlayerData(player));
    }
    default boolean isBlockOccluding(AbstractBlockLocation location, PlayerData player) {
        return isBlockOccluding(location, player.getPlayerUUID());
    }
    default Set<BlockLocation> getTileEntitiesInChunk(UUID world, int chunkX, int chunkZ, UUID player) {
        return getTileEntitiesInChunk(world, chunkX, chunkZ, PlayerRegistry.getInstance().getPlayerData(player));
    }
    default Set<BlockLocation> getTileEntitiesInChunk(UUID world, int chunkX, int chunkZ, PlayerData player) {
        return getTileEntitiesInChunk(world, chunkX, chunkZ, player.getPlayerUUID());
    }

    SnapshotManager.BlockSnapshotManagerType getType();
}
