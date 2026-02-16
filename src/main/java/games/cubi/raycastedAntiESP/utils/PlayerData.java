package games.cubi.raycastedAntiESP.utils;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;

import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.block.BlockSnapshotManager;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    protected record VisibilityAndLastCheckTime(boolean visible, int lastChecked) {}

    private final UUID playerUUID;
    private final int joinTick;
    private volatile boolean hasBypassPermission;

    public static class TileEntityVisibilityTracker extends VisibilityTracker<AbstractBlockLocation> {
        private final ConcurrentHashMap<AbstractBlockLocation, VisibilityAndLastCheckTime> tileEntityVisibility = new ConcurrentHashMap<>();

        @Override
        protected ConcurrentHashMap<AbstractBlockLocation, VisibilityAndLastCheckTime> getMap() {
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
    }

    public static class EntityVisibilityTracker extends VisibilityTracker<UUID> {
        private final ConcurrentHashMap<UUID, VisibilityAndLastCheckTime> entityVisibility = new ConcurrentHashMap<>();     // UUID = Entity UUID, Boolean = if it is visible to the player. False = hidden

        @Override
        protected ConcurrentHashMap<UUID, VisibilityAndLastCheckTime> getMap() {
            return entityVisibility;
        }
    }

    public static class PlayerVisibilityTracker extends VisibilityTracker<UUID> {
        private final ConcurrentHashMap<UUID, VisibilityAndLastCheckTime> playerVisibility = new ConcurrentHashMap<>();    // UUID = Entity UUID, Boolean = if it is visible to the player. False = hidden

        @Override
        protected ConcurrentHashMap<UUID, VisibilityAndLastCheckTime> getMap() {
            return playerVisibility;
        }
    }

    private final TileEntityVisibilityTracker tileEntityVisibilityTracker = new TileEntityVisibilityTracker();
    private final EntityVisibilityTracker entityVisibilityTracker = new EntityVisibilityTracker();
    private final PlayerVisibilityTracker playerVisibilityTracker = new PlayerVisibilityTracker();

    public PlayerData(UUID player, boolean hasBypassPermission, int joinTick) {
        this.joinTick = joinTick;
        this.playerUUID = player;
        this.hasBypassPermission = hasBypassPermission;
    }

    public TileEntityVisibilityTracker tileVisibility() {
        return tileEntityVisibilityTracker;
    }

    public EntityVisibilityTracker entityVisibility() {
        return entityVisibilityTracker;
    }

    public PlayerVisibilityTracker playerVisibility() {
        return playerVisibilityTracker;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean hasBypassPermission() {
        return hasBypassPermission;
    }

    public void setBypassPermission(boolean hasBypassPermission) {
        this.hasBypassPermission = hasBypassPermission;
    } //todo: need to link up
}
