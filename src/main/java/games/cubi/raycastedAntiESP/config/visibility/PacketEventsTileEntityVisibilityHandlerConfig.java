package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class PacketEventsTileEntityVisibilityHandlerConfig extends TileEntityVisibilityHandlerConfig {
    protected PacketEventsTileEntityVisibilityHandlerConfig(TileEntityVisibilityHandlerMode mode) {
        super(mode);
    }

    public static final PacketEventsTileEntityVisibilityHandlerConfig DEFAULT =
        new PacketEventsTileEntityVisibilityHandlerConfig(TileEntityVisibilityHandlerMode.PACKETEVENTS);

    static class Factory implements ConfigFactory<PacketEventsTileEntityVisibilityHandlerConfig> {
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
        public @NotNull PacketEventsTileEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                                    PacketEventsTileEntityVisibilityHandlerConfig defaults) {
            return new PacketEventsTileEntityVisibilityHandlerConfig(mode);
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsTileEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                                  PacketEventsTileEntityVisibilityHandlerConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
