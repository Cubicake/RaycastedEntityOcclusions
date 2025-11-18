package games.cubi.raycastedAntiESP.snapshot;

public class SnapshotManager {
    private static SnapshotManager instance;

    private BlockSnapshotManager blockSnapshotManager;
    private EntitySnapshotManager entitySnapshotManager;

    private SnapshotManager() {}

    public static SnapshotManager get() {
        if (instance == null) {
            instance = new SnapshotManager();
        }
        return instance;
    }

    public BlockSnapshotManager getBlockSnapshotManager() {
        return blockSnapshotManager;
    }

    public EntitySnapshotManager getEntitySnapshotManager() {
        return entitySnapshotManager;
    }

}
