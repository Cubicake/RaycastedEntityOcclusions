package games.cubi.raycastedAntiESP.config.visibility.tileentity;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BukkitTileEntityVisibilityHandlerConfig extends TileEntityVisibilityHandlerConfig {
    protected BukkitTileEntityVisibilityHandlerConfig() {
        super(TileEntityVisibilityHandlerMode.BUKKIT);
    }

    public static final BukkitTileEntityVisibilityHandlerConfig DEFAULT =
        new BukkitTileEntityVisibilityHandlerConfig();

    static class Factory implements ConfigFactory<BukkitTileEntityVisibilityHandlerConfig> {

        Factory() {}

        @Override
        public String getFullPath() {
            return getPathUpToTileEntity() + TileEntityVisibilityHandlerMode.BUKKIT.getPathName();
        }

        private String getPathUpToTileEntity() {
            return VisibilityHandlersConfig.Factory.PATH + TileEntityVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull BukkitTileEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config) {
            return new BukkitTileEntityVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<BukkitTileEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
