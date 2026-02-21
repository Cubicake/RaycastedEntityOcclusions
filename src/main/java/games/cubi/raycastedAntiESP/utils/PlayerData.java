package games.cubi.raycastedAntiESP.utils;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    protected record VisibilityAndLastCheckTime(boolean visible, int lastChecked) {}

    private final UUID playerUUID;
    private final int joinTick;
    private volatile boolean hasBypassPermission;

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

    private final TileEntityVisibilityTracker tileEntityVisibilityTracker = new TileEntityVisibilityTracker(this);
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

    public int getJoinTick() {
        return joinTick;
    }

    public void setBypassPermission(boolean hasBypassPermission) {
        this.hasBypassPermission = hasBypassPermission;
    } //todo: need to link up
}
