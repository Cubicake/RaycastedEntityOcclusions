package games.cubi.raycastedAntiESP.config;

public class SnapshotConfig {
    private final short worldSnapshotRefreshInterval;
    private final short entityLocationRefreshInterval;
    private final boolean performUnsafeWorldSnapshots;

    public SnapshotConfig(short worldRefresh, short entityRefresh, boolean doUnsafeWorldSnapshots) {
        worldSnapshotRefreshInterval = worldRefresh;
        entityLocationRefreshInterval = entityRefresh;
        performUnsafeWorldSnapshots = doUnsafeWorldSnapshots;
    }

    public SnapshotConfig(int worldRefresh, int entityRefresh, boolean doUnsafeWorldSnapshots) {
        worldSnapshotRefreshInterval = (short) worldRefresh;
        entityLocationRefreshInterval = (short) entityRefresh;
        performUnsafeWorldSnapshots = doUnsafeWorldSnapshots;
    }

    public short getWorldSnapshotRefreshInterval() {
        return worldSnapshotRefreshInterval;
    }

    public short getEntityLocationRefreshInterval() {
        return entityLocationRefreshInterval;
    }

    public boolean performUnsafeWorldSnapshots() {
        return performUnsafeWorldSnapshots;
    }
}
