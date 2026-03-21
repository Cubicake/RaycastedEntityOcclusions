package games.cubi.raycastedantiesp.paper.snapshot.block.packet;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import games.cubi.raycastedantiesp.paper.Logger;
import games.cubi.locatables.block.BlockLocatable;
import games.cubi.locatables.block.ImmutableBlockLocatable;
import games.cubi.raycastedantiesp.paper.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.paper.snapshot.block.BlockSnapshotManager;
import games.cubi.raycastedantiesp.paper.utils.PlayerData;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketEventsBSM implements BlockSnapshotManager {

    record PlayerSnapshot(OcclusionStateStore occlusionStateStore, TileEntityStateStore<NBTCompound> tileEntityStateStore) {}

    private final ConcurrentHashMap<UUID, PlayerSnapshot> playerSnapshots = new ConcurrentHashMap<>();

    public PacketEventsBSM() {

    }

    @Override
    public boolean isBlockOccluding(BlockLocatable location, PlayerData player) {
        PlayerSnapshot snapshot = playerSnapshots.get(player.getPlayerUUID());
        if (snapshot == null) {
            Logger.warning("PacketEventsBSM: No snapshot for player " + player, 5);
            return false;
        }
        return snapshot.occlusionStateStore().isOccluding(location);
    }

    @Override
    public Set<ImmutableBlockLocatable> getTileEntitiesInChunk(UUID world, int chunkX, int chunkZ, PlayerData player) {
        PlayerSnapshot snapshot = playerSnapshots.get(player.getPlayerUUID());
        if (snapshot == null) {
            Logger.warning("PacketEventsBSM: No snapshot for player " + player, 5);
            return null;
        }
        Set<PacketTileEntity<NBTCompound>> tileEntities = snapshot.tileEntityStateStore.getTileEntitiesInChunk(world, chunkX, chunkZ);
        return tileEntities.stream().map(PacketTileEntity::location).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public SnapshotManager.BlockSnapshotManagerType getType() {
        return SnapshotManager.BlockSnapshotManagerType.PACKETEVENTS;
    }
}
