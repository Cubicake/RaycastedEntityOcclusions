package games.cubi.raycastedantiesp.core.view;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;

public final class ViewRegistry {
    private static EntityView.Factory entityViewFactory;
    private static TileEntityView.Factory tileEntityViewFactory;
    private static PlayerBlockSnapshotManager.Factory blockSnapshotFactory;

    private ViewRegistry() {}

    public static void initialise(
            PlayerBlockSnapshotManager.Factory blockSnapshotFactory1,
            EntityView.Factory entityViewFactory1,
            TileEntityView.Factory tileEntityViewFactory1
    ) {
        blockSnapshotFactory = blockSnapshotFactory1;
        entityViewFactory = entityViewFactory1;
        tileEntityViewFactory = tileEntityViewFactory1;
    }

    public static PlayerBlockSnapshotManager createBlockSnapshotManager() {
        if (blockSnapshotFactory == null) {
            Logger.error(new IllegalStateException("Block snapshot factory is null. Did you forget to initialise ViewRegistry?"), 1, ViewRegistry.class);
        }
        return blockSnapshotFactory.createPlayerBlockSnapshotManager();
    }

    public static EntityView<?> createEntityView() {
        if (entityViewFactory == null) {
            Logger.error(new IllegalStateException("Entity view factory is null. Did you forget to initialise ViewRegistry?"), 1, ViewRegistry.class);
        }
        return entityViewFactory.createEntityView();
    }

    public static TileEntityView createTileEntityView() {
        if (tileEntityViewFactory == null) {
            Logger.error(new IllegalStateException("Tile entity view factory is null. Did you forget to initialise ViewRegistry?"), 1, ViewRegistry.class);
        }
        return tileEntityViewFactory.createTileEntityView();
    }
}
