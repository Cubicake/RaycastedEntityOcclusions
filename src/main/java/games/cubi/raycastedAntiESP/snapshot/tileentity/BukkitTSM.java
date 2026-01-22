package games.cubi.raycastedAntiESP.snapshot.tileentity;

import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitTSM extends PlayerLastSeenTracker {
    private record ChunkKey(UUID world, int x, int z) {}

    private final ConcurrentHashMap<ChunkKey, Set<BlockLocation>> tileEntitiesByChunk = new ConcurrentHashMap<>();

    @Override
    public int getTicksSincePlayerSawTileEntity(UUID player, AbstractBlockLocation tileEntity) {
        int currentTick = DataHolder.getTick();
        for (PlayerLastCheckTimestamp timestamp : getPlayerLastSeenTimestamps(tileEntity)) {
            if (!timestamp.getPlayer().equals(player)) continue;
            return timestamp.hasBeenSeen() ? currentTick - timestamp.getTimestamp() : -1;
        }
        return -1;
    }

    @Override
    public Set<BlockLocation> getTileEntitiesInChunk(UUID world, int x, int z) {
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null || !bukkitWorld.isChunkLoaded(x, z)) {
            return Set.of();
        }
        ChunkKey key = new ChunkKey(world, x, z);
        Set<BlockLocation> cached = tileEntitiesByChunk.computeIfAbsent(key, ignored -> snapshotChunk(bukkitWorld, x, z));
        return Set.copyOf(cached);
    }

    @Override
    public SnapshotManager.TileEntitySnapshotManagerType getType() {
        return SnapshotManager.TileEntitySnapshotManagerType.BUKKIT;
    }

    private Set<BlockLocation> snapshotChunk(World world, int x, int z) {
        Chunk chunk = world.getChunkAt(x, z);
        Set<BlockLocation> tileEntities = ConcurrentHashMap.newKeySet();
        for (BlockState state : chunk.getTileEntities()) {
            Material material = state.getType();
            if (ConfigManager.get().getTileEntityConfig().getExemptedBlocks().contains(material)) {
                continue;
            }
            tileEntities.add(new BlockLocation(state.getLocation()));
        }
        return tileEntities;
    }
}
