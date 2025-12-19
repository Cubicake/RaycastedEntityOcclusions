package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import games.cubi.raycastedAntiESP.visibilitychangehandlers.entity.EntityVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.player.PlayerVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity.TileEntityVisibilityChanger;
import org.jspecify.annotations.Nullable;

public class VisibilityChangeHandlers {
    private static EntityVisibilityChanger entityVisibilityChanger;
    private static PlayerVisibilityChanger playerVisibilityChanger;
    private static TileEntityVisibilityChanger tileEntityVisibilityChanger;

    public enum EntityVisibilityChangerType {
        BUKKIT,
    }
    public enum PlayerVisibilityChangerType {
        BUKKIT,
    }
    public enum TileEntityVisibilityChangerType {
        BUKKIT,
    }

    private static EntityVisibilityChangerType entityVisibilityChangeHandlerType;
    private static PlayerVisibilityChangerType playerVisibilityChangeHandlerType;
    private static TileEntityVisibilityChangerType tileEntityVisibilityChangeHandlerType;

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

    public static EntityVisibilityChangerType entityVisibilityChangeHandlerType() {
        return entityVisibilityChangeHandlerType;
    }
    public static PlayerVisibilityChangerType playerVisibilityChangeHandlerType() {
        return playerVisibilityChangeHandlerType;
    }
    public static TileEntityVisibilityChangerType tileEntityVisibilityChangeHandlerType() {
        return tileEntityVisibilityChangeHandlerType;
    }

    public static void changeHandlers(EntityVisibilityChanger entityVisibilityChanger1, PlayerVisibilityChanger playerVisibilityChanger1, TileEntityVisibilityChanger tileEntityVisibilityChanger1) {
        entityVisibilityChanger = entityVisibilityChanger1;
        playerVisibilityChanger = playerVisibilityChanger1;
        tileEntityVisibilityChanger = tileEntityVisibilityChanger1;

        entityVisibilityChangeHandlerType = entityVisibilityChanger1.getType();
        playerVisibilityChangeHandlerType = playerVisibilityChanger1.getType();
        tileEntityVisibilityChangeHandlerType = tileEntityVisibilityChanger1.getType();
    }
}
