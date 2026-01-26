package games.cubi.raycastedAntiESP.config.snapshot.block;

public abstract class BukkitBlockSnapshotConfig implements BlockSnapshotConfig {
    private final int refreshRateSeconds;

    public BukkitBlockSnapshotConfig(int refreshRateSeconds) {
        this.refreshRateSeconds = refreshRateSeconds;
    }

    public int getRefreshRateSeconds() {
        return refreshRateSeconds;
    }
}
