package games.cubi.raycastedAntiESP.snapshot.block.packet;

import games.cubi.raycastedAntiESP.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TileEntityStateStore<T> {
    private final ConcurrentHashMap<Chunk, Set<PacketTileEntity<T>>> tileEntities = new ConcurrentHashMap<>();
    private final OcclusionStateStore occlusionStateStore;

    protected TileEntityStateStore(OcclusionStateStore occlusionData) {
        this.occlusionStateStore = occlusionData;
    }
/*
    protected @Nullable PacketTileEntity<T> getTileEntity(UUID world, final int x, final int y, final int z) {
        Set<PacketTileEntity<T>> worldData = tileEntities.get(new Chunk(world, x >> 4, z >> 4));
        if (worldData == null) {
            Logger.warning("No tile entity data for world " + world, 5);
            return null;
        }
        int localX = x & 15;
        int localZ = z & 15;
        int localY = y - occlusionStateStore.getMinimumWorldHeight(world);
        return worldData[localX][localY][localZ];
    }*/
    protected Set<PacketTileEntity<T>> getTileEntitiesInChunk(UUID world, int chunkX, int chunkZ) {
        return tileEntities.get(new Chunk(world, chunkX, chunkZ));
    }

    protected void setTileEntities(UUID world, int chunkX, int chunkZ, Set<PacketTileEntity<T>> data) {
        Set<PacketTileEntity<T>> concurrentSet = ConcurrentHashMap.newKeySet();
        concurrentSet.addAll(data);
        tileEntities.put(new Chunk(world, chunkX, chunkZ), concurrentSet);
    }

    protected void setTileEntity(UUID world, int x, int y, int z, PacketTileEntity<T> tileEntity) {
        Chunk chunk = new Chunk(world, x >> 4, z >> 4);
        Set<PacketTileEntity<T>> chunkData = tileEntities.get(chunk);
        if (chunkData == null) {
            Logger.warning("No occlusion data for world " + world + ", cannot set occlusion state", 5);
            return;
        }
        chunkData.add(tileEntity);
    }

    protected void removeTileEntity(UUID world, int x, int y, int z) {
        Chunk chunk = new Chunk(world, x >> 4, z >> 4);
        Set<PacketTileEntity<T>> chunkData = tileEntities.get(chunk);
        if (chunkData == null) {
            Logger.warning("No occlusion data for world " + world + ", cannot set occlusion state", 5);
            return;
        }
        chunkData.removeIf(te -> te.location().blockX() == x && te.location().y() == y && te.location().z() == z);
    }

    protected void removeChunkData(UUID world, int chunkX, int chunkZ) {
        tileEntities.remove(new Chunk(world, chunkX, chunkZ));
    }
}
