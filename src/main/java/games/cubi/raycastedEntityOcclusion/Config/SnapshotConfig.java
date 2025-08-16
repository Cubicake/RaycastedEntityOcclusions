package games.cubi.raycastedEntityOcclusion.Config;

public class SnapshotConfig {
    private final short worldSnapshotRefreshInterval;
    private final short entityLocationRefreshInterval;

    public SnapshotConfig(short worldRefresh, short entityRefresh) {
        worldSnapshotRefreshInterval = worldRefresh;
        entityLocationRefreshInterval = entityRefresh;
    }

    public SnapshotConfig(int worldRefresh, int entityRefresh) {
        worldSnapshotRefreshInterval = (short) worldRefresh;
        entityLocationRefreshInterval = (short) entityRefresh;
    }

    public short getWorldSnapshotRefreshInterval() {
        return worldSnapshotRefreshInterval;
    }

    public short getEntityLocationRefreshInterval() {
        return entityLocationRefreshInterval;
    }
}
