package games.cubi.raycastedAntiESP.snapshot.block;

import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitBSM implements BlockSnapshotManager {
    private record ChunkKey(UUID world, int x, int z) {}

    private final JavaPlugin plugin;
    private final ConfigManager config;
    private final ConcurrentHashMap<ChunkKey, Set<BlockLocation>> occludingBlocks = new ConcurrentHashMap<>();

    public BukkitBSM(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        snapshotLoadedChunks();
        scheduleRefresh();
    }

    @Override
    public boolean isBlockOccluding(AbstractBlockLocation location) {
        ChunkKey key = new ChunkKey(location.world(), location.blockX() >> 4, location.blockZ() >> 4);
        Set<BlockLocation> chunkBlocks = occludingBlocks.get(key);
        if (chunkBlocks != null) {
            return chunkBlocks.contains(location);
        }
        return fallbackOcclusionCheck(location);
    }

    @Override
    public SnapshotManager.BlockSnapshotManagerType getType() {
        return SnapshotManager.BlockSnapshotManagerType.BUKKIT;
    }

    public void onChunkLoad(Chunk chunk) {
        snapshotChunk(chunk);
    }

    public void onChunkUnload(Chunk chunk) {
        occludingBlocks.remove(new ChunkKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ()));
    }

    public void onBlockChange(Location location, Material material, int type) {
        if (location.getWorld() == null) return;
        Chunk chunk = location.getChunk();
        ChunkKey key = new ChunkKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        Set<BlockLocation> chunkBlocks = occludingBlocks.get(key);
        if (chunkBlocks == null) {
            snapshotChunk(chunk);
            return;
        }
        BlockLocation blockLocation = new BlockLocation(location);
        if (material.isOccluding()) {
            chunkBlocks.add(blockLocation);
        } else {
            chunkBlocks.remove(blockLocation);
        }
    }

    private void snapshotLoadedChunks() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                snapshotChunk(chunk);
            }
        }
    }

    private void snapshotChunk(Chunk chunk) {
        Set<BlockLocation> chunkBlocks = ConcurrentHashMap.newKeySet();
        World world = chunk.getWorld();
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();
        int chunkX = chunk.getX() << 4;
        int chunkZ = chunk.getZ() << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    Material material = world.getBlockAt(chunkX + x, y, chunkZ + z).getType();
                    if (material.isOccluding()) {
                        chunkBlocks.add(new BlockLocation(world, chunkX + x, y, chunkZ + z));
                    }
                }
            }
        }
        occludingBlocks.put(new ChunkKey(world.getUID(), chunk.getX(), chunk.getZ()), chunkBlocks);
    }

    private boolean fallbackOcclusionCheck(AbstractBlockLocation location) {
        if (!shouldAccessWorld()) {
            queueChunkSnapshot(location.world(), location.blockX() >> 4, location.blockZ() >> 4);
            return true;
        }
        World world = Bukkit.getWorld(location.world());
        if (world == null) return true;
        int chunkX = location.blockX() >> 4;
        int chunkZ = location.blockZ() >> 4;
        if (!world.isChunkLoaded(chunkX, chunkZ)) return true;
        Material material = world.getBlockAt(location.blockX(), location.blockY(), location.blockZ()).getType();
        return material.isOccluding();
    }

    private boolean shouldAccessWorld() {
        return Bukkit.isPrimaryThread() || config.getSnapshotConfig().performUnsafeWorldSnapshots();
    }

    private void queueChunkSnapshot(UUID worldId, int chunkX, int chunkZ) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            World world = Bukkit.getWorld(worldId);
            if (world == null || !world.isChunkLoaded(chunkX, chunkZ)) return;
            snapshotChunk(world.getChunkAt(chunkX, chunkZ));
        });
    }

    private void scheduleRefresh() {
        short interval = config.getSnapshotConfig().getWorldSnapshotRefreshInterval();
        if (interval <= 0) return;
        long ticks = interval * 20L;
        Bukkit.getScheduler().runTaskTimer(plugin, this::snapshotLoadedChunks, ticks, ticks);
    }
}
