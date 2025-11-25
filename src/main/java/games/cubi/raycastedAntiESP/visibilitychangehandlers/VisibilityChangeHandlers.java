package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import org.jspecify.annotations.Nullable;

public class VisibilityChangeHandlers {
    private static EntityVisibilityChanger entityVisibilityChanger;
    private static PlayerVisibilityChanger playerVisibilityChanger;
    private static TileEntityVisibilityChanger tileEntityVisibilityChanger;

    public static void initialise(EntityVisibilityChanger entityVisibilityChanger1, PlayerVisibilityChanger playerVisibilityChanger1, TileEntityVisibilityChanger tileEntityVisibilityChanger1) {
        changeHandlers(entityVisibilityChanger1, playerVisibilityChanger1, tileEntityVisibilityChanger1);
    }

    @Nullable
    public static EntityVisibilityChanger getEntity() {
        return entityVisibilityChanger;
    }

    @Nullable
    public static PlayerVisibilityChanger getPlayer() {
        return playerVisibilityChanger;
    }

    @Nullable
    public static TileEntityVisibilityChanger getTileEntity() {
        return tileEntityVisibilityChanger;
    }

    public static void changeHandlers(EntityVisibilityChanger entityVisibilityChanger1, PlayerVisibilityChanger playerVisibilityChanger1, TileEntityVisibilityChanger tileEntityVisibilityChanger1) {
        entityVisibilityChanger = entityVisibilityChanger1;
        playerVisibilityChanger = playerVisibilityChanger1;
        tileEntityVisibilityChanger = tileEntityVisibilityChanger1;
    }
}
