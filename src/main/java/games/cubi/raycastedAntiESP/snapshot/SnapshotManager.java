package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.snapshot.block.BlockSnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.entity.EntitySnapshotManager;

public class SnapshotManager {
    private static BlockSnapshotManager blockSnapshotManager;
    private static EntitySnapshotManager entitySnapshotManager;

    private SnapshotManager() {}

    public enum EntitySnapshotManagerType {
        BUKKIT,
    }

    public enum BlockSnapshotManagerType {
        BUKKIT,
    }

    private static EntitySnapshotManagerType entitySnapshotManagerType;
    private static BlockSnapshotManagerType blockSnapshotManagerType;

    public static void initialise(BlockSnapshotManager blockSnapshotManager1, EntitySnapshotManager entitySnapshotManager1) {
        changeManagers(blockSnapshotManager1, entitySnapshotManager1);
    }

    public static BlockSnapshotManager getBlockSnapshotManager() {
        return blockSnapshotManager;
    }

    public static EntitySnapshotManager getEntitySnapshotManager() {
        return entitySnapshotManager;
    }


    public static EntitySnapshotManagerType entitySnapshotManagerType() {
        return entitySnapshotManagerType;
    }

    public static BlockSnapshotManagerType blockSnapshotManagerType() {
        return blockSnapshotManagerType;
    }

    public static void changeManagers(BlockSnapshotManager blockSnapshotManager1, EntitySnapshotManager entitySnapshotManager1) {
        blockSnapshotManager = blockSnapshotManager1;
        entitySnapshotManager = entitySnapshotManager1;

        entitySnapshotManagerType = entitySnapshotManager1.getType();
        blockSnapshotManagerType = blockSnapshotManager1.getType();
    }
}