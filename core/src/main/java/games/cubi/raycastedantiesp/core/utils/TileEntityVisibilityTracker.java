package games.cubi.raycastedantiesp.core.utils;

import games.cubi.raycastedantiesp.core.Logger;
import games.cubi.raycastedantiesp.core.locatables.Locatable;
import games.cubi.locatables.block.BlockLocatable;
import games.cubi.raycastedantiesp.core.snapshot.block.BlockSnapshotManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TileEntityVisibilityTracker extends VisibilityTracker<BlockLocatable> {
    private final ConcurrentHashMap<BlockLocatable, PlayerData.VisibilityAndLastCheckTime> tileEntityVisibility = new ConcurrentHashMap<>();
    private final Set<Long> loadedChunks = ConcurrentHashMap.newKeySet();
    private final PlayerData player;

    public TileEntityVisibilityTracker(PlayerData player) {
        this.player = player;
    }

    @Override
    protected ConcurrentHashMap<BlockLocatable, PlayerData.VisibilityAndLastCheckTime> getMap() {
        return tileEntityVisibility;
    }

    @Override
    public Set<BlockLocatable> getNeedingRecheck(int recheckTicks, int currentTime) {
        Logger.errorAndReturn(new RuntimeException("getNeedingRecheck without world and chunk parameters called on TileEntityVisibilityTracker."), 1);
        return null;
    }

    public Set<BlockLocatable> getNeedingRecheck(int recheckTicks, int currentTime, UUID world, int chunkX, int chunkZ, int chunkRadius, BlockSnapshotManager blockSnapshotManager) {
        HashSet<BlockLocatable> recheckList = new HashSet<>();

        for (int x = chunkX-chunkRadius; x <= chunkRadius+chunkX; x++) {
            for (int z = chunkZ-chunkRadius; z <= chunkRadius+chunkZ; z++) {
                recheckList.addAll(blockSnapshotManager.getTileEntitiesInChunk(world, x, z, player));
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