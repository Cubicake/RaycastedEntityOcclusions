package games.cubi.raycastedantiesp.core.snapshot;

import games.cubi.logs.Logger;

public final class SnapshotManager {
    private static PlayerEntitySnapshotManager.Factory entitySnapshotManagerFactory;
    private static PlayerBlockSnapshotManager.Factory blockSnapshotManagerFactory;

    private SnapshotManager() {}

    public enum SnapshotManagerType {
        BUKKIT,
        PACKETEVENTS,
    }

    private static SnapshotManagerType entitySnapshotManagerType;
    private static SnapshotManagerType blockSnapshotManagerType;

    public static void initialise(PlayerBlockSnapshotManager.Factory blockSnapshotManager, PlayerEntitySnapshotManager.Factory entitySnapshotManager) {
        changeManagers(blockSnapshotManager, entitySnapshotManager);
    }

    public static PlayerEntitySnapshotManager createEntitySnapshotManager() {
        if (entitySnapshotManagerFactory == null) {
            Logger.error(new IllegalStateException("EntitySnapshotManagerFactory is null! Did you forget to call SnapshotManager#initialise?"), 1, SnapshotManager.class);
        }
        return entitySnapshotManagerFactory.createPlayerEntitySnapshotManager();
    }

    public static PlayerBlockSnapshotManager createBlockSnapshotManager() {
        if (blockSnapshotManagerFactory == null) {
            Logger.error(new IllegalStateException("BlockSnapshotManagerFactory is null! Did you forget to call SnapshotManager#initialise?"), 1, SnapshotManager.class);
        }
        return blockSnapshotManagerFactory.createPlayerBlockSnapshotManager();
    }

    public static SnapshotManagerType entitySnapshotManagerType() {
        return entitySnapshotManagerType;
    }

    public static SnapshotManagerType blockSnapshotManagerType() {
        return blockSnapshotManagerType;
    }

    public static void changeManagers(PlayerBlockSnapshotManager.Factory blockSnapshotManager, PlayerEntitySnapshotManager.Factory entitySnapshotManager) {
        blockSnapshotManagerFactory = blockSnapshotManager;
        entitySnapshotManagerFactory = entitySnapshotManager;

        entitySnapshotManagerType = entitySnapshotManager.getType();
        blockSnapshotManagerType = blockSnapshotManager.getType();
    }
}