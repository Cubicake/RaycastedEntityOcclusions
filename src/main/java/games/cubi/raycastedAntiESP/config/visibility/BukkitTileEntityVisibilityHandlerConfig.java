package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BukkitTileEntityVisibilityHandlerConfig extends TileEntityVisibilityHandlerConfig {
    protected BukkitTileEntityVisibilityHandlerConfig(TileEntityVisibilityHandlerMode mode) {
        super(mode);
    }

    public static final BukkitTileEntityVisibilityHandlerConfig DEFAULT =
        new BukkitTileEntityVisibilityHandlerConfig(TileEntityVisibilityHandlerMode.BUKKIT);

    static class Factory implements ConfigFactory<BukkitTileEntityVisibilityHandlerConfig> {
        private final TileEntityVisibilityHandlerMode mode;

        Factory(TileEntityVisibilityHandlerMode mode) {
            this.mode = mode;
        }

        @Override
        public String getFullPath() {
            return getPathUpToTileEntity() + mode.getPathName();
        }

        private String getPathUpToTileEntity() {
            return VisibilityHandlersConfig.Factory.PATH + TileEntityVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull BukkitTileEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                              BukkitTileEntityVisibilityHandlerConfig defaults) {
            return new BukkitTileEntityVisibilityHandlerConfig(mode);
        }

        @Override
        public @NotNull ConfigFactory<BukkitTileEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                            BukkitTileEntityVisibilityHandlerConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
