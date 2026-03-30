package games.cubi.raycastedantiesp.core.snapshot.block;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;

import java.util.Set;
import java.util.UUID;

public interface BlockSnapshotManager {
    default boolean isBlockOccluding(BlockLocatable location, UUID player) {
        return isBlockOccluding(location, PlayerRegistry.getInstance().getPlayerData(player));
    }
    default boolean isBlockOccluding(BlockLocatable location, PlayerData player) {
        return isBlockOccluding(location, player.getPlayerUUID());
    }
    default Set<ImmutableBlockLocatable> getTileEntitiesInChunk(UUID world, int chunkX, int chunkZ, UUID player) {
        return getTileEntitiesInChunk(world, chunkX, chunkZ, PlayerRegistry.getInstance().getPlayerData(player));
    }
    default Set<ImmutableBlockLocatable> getTileEntitiesInChunk(UUID world, int chunkX, int chunkZ, PlayerData player) {
        return getTileEntitiesInChunk(world, chunkX, chunkZ, player.getPlayerUUID());
    }

    SnapshotManager.BlockSnapshotManagerType getType();
}
