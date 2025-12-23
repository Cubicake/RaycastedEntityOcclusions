package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.snapshot.block.BlockSnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.entity.EntitySnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.tileentity.TileEntitySnapshotManager;

public class SnapshotManager {
    private static BlockSnapshotManager blockSnapshotManager;
    private static EntitySnapshotManager entitySnapshotManager;
    private static TileEntitySnapshotManager tileEntitySnapshotManager;

    private SnapshotManager() {}

    public enum EntitySnapshotManagerType {
        BUKKIT,
    }

    public enum BlockSnapshotManagerType {
        BUKKIT,
    }

    public enum TileEntitySnapshotManagerType {
        BUKKIT,
    }

    private static EntitySnapshotManagerType entitySnapshotManagerType;
    private static BlockSnapshotManagerType blockSnapshotManagerType;
    private static TileEntitySnapshotManagerType tileEntitySnapshotManagerType;

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

    public static EntitySnapshotManagerType entitySnapshotManagerType() {
        return entitySnapshotManagerType;
    }

    public static BlockSnapshotManagerType blockSnapshotManagerType() {
        return blockSnapshotManagerType;
    }

    public static TileEntitySnapshotManagerType tileEntitySnapshotManagerType() {
        return tileEntitySnapshotManagerType;
    }

    public static void changeManagers(BlockSnapshotManager blockSnapshotManager1, EntitySnapshotManager entitySnapshotManager1, TileEntitySnapshotManager tileEntitySnapshotManager1) {
        blockSnapshotManager = blockSnapshotManager1;
        entitySnapshotManager = entitySnapshotManager1;
        tileEntitySnapshotManager = tileEntitySnapshotManager1;

        entitySnapshotManagerType = entitySnapshotManager1.getType();
        blockSnapshotManagerType = blockSnapshotManager1.getType();
        tileEntitySnapshotManagerType = tileEntitySnapshotManager1.getType();
    }
}