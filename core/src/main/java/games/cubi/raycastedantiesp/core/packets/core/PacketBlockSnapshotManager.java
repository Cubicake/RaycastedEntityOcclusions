package games.cubi.raycastedantiesp.core.packets.core;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.packets.api.BlockSnapshotPacketSink;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PacketBlockSnapshotManager implements PlayerBlockSnapshotManager, BlockSnapshotPacketSink {

    private final Map<UUID /*World*/, ConcurrentHashMap<Long, ChunkSnapshot>> worldChunks = new ConcurrentHashMap<>();

    public boolean isBlockOccluding(UUID world, int x, int y, int z) {
        final ChunkSnapshot chunk = getChunk(world, x >> 4, y >> 4, z >> 4);
        if (chunk == null) {
            Logger.warning("isBlockOccluding called for block in chunk that doesn't exist in snapshot manager. This likely means the chunk was never loaded or has been unloaded since it was loaded. Returning false. World: " + world + ", x: " + x + ", y: " + y + ", z: " + z, 5, PacketBlockSnapshotManager.class);
            return false;
        }

        return chunk.occludingBlocks.contains(packBlock(x, y, z));
    }

    @Override
    public boolean isBlockOccluding(BlockLocatable location) {
        return isBlockOccluding(location.world(), location.blockX(), location.blockY(), location.blockZ());
    }

    @Override
    public Set<ImmutableBlockLocatable> getKnownTileEntities() {
        final Set<ImmutableBlockLocatable> out = new HashSet<>();
        for (Map.Entry<UUID, ConcurrentHashMap<Long, ChunkSnapshot>> worldEntry : worldChunks.entrySet()) {
            UUID world = worldEntry.getKey();
            Map<Long, ChunkSnapshot> chunks = worldEntry.getValue();
            if (chunks == null || chunks.isEmpty()) {
                continue;
            }
            for (Map.Entry<Long, ChunkSnapshot> entry : chunks.entrySet()) {
                final ChunkSnapshot chunk = entry.getValue();
                if (chunk.tileEntityBlocks.isEmpty()) {
                    continue;
                }

                final long packedChunk = entry.getKey();
                final int chunkX = unpackChunkX(packedChunk);
                final int chunkY = unpackChunkY(packedChunk);
                final int chunkZ = unpackChunkZ(packedChunk);
                for (short packedPos : chunk.tileEntityBlocks) {
                    out.add(unpackBlock(world, chunkX, chunkY, chunkZ, packedPos));
                }
            }
        }
        return out;
    }

    @Override
    public void upsertBlock(UUID world, int x, int y, int z, boolean occluding, boolean tileEntity) {
        final ChunkSnapshot chunk = getOrCreateChunk(world, x >> 4, y >> 4, z >> 4);
        final short packed = packBlock(x, y, z);

        if (occluding) {
            chunk.occludingBlocks.add(packed);
        } else {
            chunk.occludingBlocks.remove(packed);
        }

        if (tileEntity) {
            chunk.tileEntityBlocks.add(packed);
        } else {
            chunk.tileEntityBlocks.remove(packed);
        }
    }

    @Override
    public void removeChunk(UUID world, int chunkX, int chunkZ) {
        final Map<Long, ChunkSnapshot> chunks = worldChunks.get(world);
        if (chunks == null) {
            return;
        }

        chunks.entrySet().removeIf(entry ->
                unpackChunkX(entry.getKey()) == chunkX && unpackChunkZ(entry.getKey()) == chunkZ
        );
        if (chunks.isEmpty()) {
            worldChunks.remove(world);
        }
    }

    @Override
    public void replaceChunk(UUID world, int chunkX, int chunkY, int chunkZ, Set<Short> occludingPackedPositions, Set<Short> tilePackedPositions) {
        final ChunkSnapshot chunk = getOrCreateChunk(world, chunkX, chunkY, chunkZ);
        chunk.occludingBlocks.clear();
        chunk.tileEntityBlocks.clear();
        chunk.occludingBlocks.addAll(occludingPackedPositions);
        chunk.tileEntityBlocks.addAll(tilePackedPositions);
    }

    @Override
    public void clear() {
        worldChunks.clear();
    }

    private ChunkSnapshot getChunk(UUID world, int chunkX, int chunkY, int chunkZ) {
        final Map<Long, ChunkSnapshot> chunks = worldChunks.get(world);
        if (chunks == null) {
            return null;
        }
        return chunks.get(packChunk(chunkX, chunkY, chunkZ));
    }

    private ChunkSnapshot getOrCreateChunk(UUID world, int chunkX, int chunkY, int chunkZ) {
        final Map<Long, ChunkSnapshot> chunks = worldChunks.computeIfAbsent(world, ignored -> new ConcurrentHashMap<>());
        return chunks.computeIfAbsent(packChunk(chunkX, chunkY, chunkZ), ignored -> new ChunkSnapshot());
    }

    private static long packChunk(int chunkX, int chunkY, int chunkZ) {
        return ((chunkX & 0x3ffffffL) << 38) | ((chunkZ & 0x3ffffffL) << 12) | (chunkY & 0xfffL);
    }

    private static int unpackChunkX(long packed) {
        return (int) (packed >> 38);
    }

    private static int unpackChunkY(long packed) {
        final int y = (int) (packed & 0xfffL);
        return y >= 2048 ? y - 4096 : y;
    }

    private static int unpackChunkZ(long packed) {
        return (int) ((packed << 26) >> 38);
    }

    public static short packBlock(int x, int y, int z) {
        final int localX = x & 0xF;
        final int localY = y & 0xF;
        final int localZ = z & 0xF;
        return (short) ((localX << 8) | (localY << 4) | localZ);
    }

    public static ImmutableBlockLocatable unpackBlock(UUID world, int chunkX, int chunkY, int chunkZ, short packed) {
        final int localX = (packed >> 8) & 0xF;
        final int localY = (packed >> 4) & 0xF;
        final int localZ = packed & 0xF;
        return new ImmutableBlockLocatable(world,
                (chunkX << 4) + localX,
                (chunkY << 4) + localY,
                (chunkZ << 4) + localZ
        );
    }

    private static final class ChunkSnapshot {
        private final Set<Short> occludingBlocks = ConcurrentHashMap.newKeySet();
        private final Set<Short> tileEntityBlocks = ConcurrentHashMap.newKeySet();
    }
}
