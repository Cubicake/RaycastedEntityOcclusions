package games.cubi.raycastedAntiESP.utils;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.Locatable;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.block.BlockSnapshotManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TileEntityVisibilityTracker extends VisibilityTracker<AbstractBlockLocation> {
    private final ConcurrentHashMap<AbstractBlockLocation, PlayerData.VisibilityAndLastCheckTime> tileEntityVisibility = new ConcurrentHashMap<>();
    private final Set<Long> loadedChunks = ConcurrentHashMap.newKeySet();

    @Override
    protected ConcurrentHashMap<AbstractBlockLocation, PlayerData.VisibilityAndLastCheckTime> getMap() {
        return tileEntityVisibility;
    }

    @Override
    public Set<AbstractBlockLocation> getNeedingRecheck(int recheckTicks, int currentTime) {
        Logger.errorAndReturn(new RuntimeException("getNeedingRecheck without world and chunk parameters called on TileEntityVisibilityTracker."), 1);
        return null;
    }

    public Set<BlockLocation> getNeedingRecheck(int recheckTicks, int currentTime, UUID world, int chunkX, int chunkZ, int chunkRadius, BlockSnapshotManager blockSnapshotManager) {
        HashSet<BlockLocation> recheckList = new HashSet<>();

        for (int x = chunkX-chunkRadius; x <= chunkRadius+chunkX; x++) {
            for (int z = chunkZ-chunkRadius; z <= chunkRadius+chunkZ; z++) {
                recheckList.addAll(blockSnapshotManager.getTileEntitiesInChunk(world, x, z));
            }
        }

        recheckList.removeAll(getNotNeedingRecheck(recheckTicks, currentTime));

        return recheckList;
    }

    public void removeChunk(UUID world, int chunkX, int chunkZ) {
        tileEntityVisibility.keySet().removeIf(blockLocation -> blockLocation.world().equals(world) && blockLocation.chunkX() == chunkX && blockLocation.chunkZ() == chunkZ);
        loadedChunks.remove(pack(chunkX, chunkZ));
    }

    private long pack(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public void addChunk(int chunkX, int chunkZ) {
        loadedChunks.add(pack(chunkX, chunkZ));
    }

    public boolean containsChunk(int chunkX, int chunkZ) {
        return loadedChunks.contains(pack(chunkX, chunkZ));
    }

    public boolean containsChunk(Locatable locatable) {
        return containsChunk(locatable.chunkX(), locatable.chunkZ());
    }

}