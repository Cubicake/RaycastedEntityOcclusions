package games.cubi.raycastedantiesp.core.view;

import games.cubi.logs.Logger;

public final class ViewRegistry {
    private static EntityView.Factory entityViewFactory;
    private static BlockView.Factory blockViewFactory;

    private ViewRegistry() {}

    public static void initialise(
            BlockView.Factory blockViewFactory1,
            EntityView.Factory entityViewFactory1
    ) {
        blockViewFactory = blockViewFactory1;
        entityViewFactory = entityViewFactory1;
    }

    public static BlockView createBlockView() {
        if (blockViewFactory == null) {
            Logger.error(new IllegalStateException("Block view factory is null. Did you forget to initialise ViewRegistry?"), 1, ViewRegistry.class);
        }
        return blockViewFactory.createBlockView();
    }

    public static EntityView<?> createEntityView() {
        if (entityViewFactory == null) {
            Logger.error(new IllegalStateException("Entity view factory is null. Did you forget to initialise ViewRegistry?"), 1, ViewRegistry.class);
        }
        return entityViewFactory.createEntityView();
    }
}
