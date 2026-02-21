package games.cubi.raycastedAntiESP.snapshot.block.packet;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// A per-player store of blocks
public class OcclusionStateStore {

    protected OcclusionStateStore() {}

    private final ConcurrentHashMap<Chunk, boolean[][][]> occlusionData = new ConcurrentHashMap<>(); // Since we shouldn't need CAS operations, this is thread-safe
    private final ConcurrentHashMap<UUID, Integer> minimumWorldHeight = new ConcurrentHashMap<>(); // todo Maybe switch to a replace-on-write map for liqhtning fast reads

    public boolean isOccluding(AbstractBlockLocation location) {
        return isOccluding(location.world(), location.blockX(), location.blockY(), location.blockZ());
    }

    public boolean isOccluding(UUID world, final int x, final int y, final int z) {
        boolean[][][] worldData = occlusionData.get(new Chunk(world, x >> 4, z >> 4));
        if (worldData == null) {
            Logger.warning("No occlusion data for world " + world + ", defaulting to non-occluding", 5);
            return false;
        }
        int localX = x & 15;
        int localZ = z & 15;
        int localY = y - minimumWorldHeight.getOrDefault(world, 0);
        return worldData[localX][localY][localZ];
    }

    protected void setOcclusionData(UUID world, int chunkX, int chunkZ, boolean[][][] data) {
        occlusionData.put(new Chunk(world, chunkX, chunkZ), data);
    }

    protected void setOcclusionData(UUID world, int x, int y, int z, boolean isOccluding) {
        Chunk chunk = new Chunk(world, x >> 4, z >> 4);
        boolean[][][] chunkData = occlusionData.get(chunk);
        if (chunkData == null) {
            Logger.warning("No occlusion data for world " + world + ", cannot set occlusion state", 5);
            return;
        }
        int localX = x & 15;
        int localZ = z & 15;
        int localY = y - minimumWorldHeight.getOrDefault(world, 0);
        chunkData[localX][localY][localZ] = isOccluding;
    }

    protected void removeChunkData(UUID world, int chunkX, int chunkZ) {
        occlusionData.remove(new Chunk(world, chunkX, chunkZ));
    }

    protected void setMinimumWorldHeight(UUID world, int minHeight) {
        minimumWorldHeight.put(world, minHeight);
    }

    protected void removeMinimumWorldHeight(UUID world) {
        minimumWorldHeight.remove(world);
    }

    protected int getMinimumWorldHeight(UUID world) {
        return minimumWorldHeight.getOrDefault(world, 0);
    }
}
