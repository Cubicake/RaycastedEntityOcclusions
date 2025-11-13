package games.cubi.raycastedAntiESP.snapshot;

public class SnapshotManager {
    private static SnapshotManager instance;

    private SnapshotManager() {}

    public static SnapshotManager get() {
        if (instance == null) {
            instance = new SnapshotManager();
        }
        return instance;
    }

    public void getBlockSnapshotManager() {}

}
