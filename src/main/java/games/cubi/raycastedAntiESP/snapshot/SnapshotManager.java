package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.visibilitychangehandlers.EntityVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.PlayerVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.TileEntityVisibilityChanger;
import org.jspecify.annotations.Nullable;

public class SnapshotManager {
    private static BlockSnapshotManager blockSnapshotManager;
    private static EntitySnapshotManager entitySnapshotManager;
    private static TileEntitySnapshotManager tileEntitySnapshotManager;

    private SnapshotManager() {}

    public static void initialise(BlockSnapshotManager blockSnapshotManager1, EntitySnapshotManager entitySnapshotManager1, TileEntitySnapshotManager tileEntitySnapshotManager1) {
        changeManagers(blockSnapshotManager1, entitySnapshotManager1, tileEntitySnapshotManager1);
    }

    public static BlockSnapshotManager getBlockSnapshotManager() {
        return blockSnapshotManager;
    }

    public static EntitySnapshotManager getEntitySnapshotManager() {
        return entitySnapshotManager;
    }

    public static TileEntitySnapshotManager getTileEntitySnapshotManager() {
        return tileEntitySnapshotManager;
    }

    public static void changeManagers(BlockSnapshotManager blockSnapshotManager1, EntitySnapshotManager entitySnapshotManager1, TileEntitySnapshotManager tileEntitySnapshotManager1) {
        blockSnapshotManager = blockSnapshotManager1;
        entitySnapshotManager = entitySnapshotManager1;
        tileEntitySnapshotManager = tileEntitySnapshotManager1;
    }
}