package games.cubi.raycastedAntiESP.config.visibility.tileentity;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class PacketEventsTileEntityVisibilityHandlerConfig extends TileEntityVisibilityHandlerConfig {
    protected PacketEventsTileEntityVisibilityHandlerConfig() {
        super(TileEntityVisibilityHandlerMode.PACKETEVENTS);
    }

    public static final PacketEventsTileEntityVisibilityHandlerConfig DEFAULT =
        new PacketEventsTileEntityVisibilityHandlerConfig();

    static class Factory implements ConfigFactory<PacketEventsTileEntityVisibilityHandlerConfig> {

        Factory() {}

        @Override
        public String getFullPath() {
            return getPathUpToTileEntity() + TileEntityVisibilityHandlerMode.PACKETEVENTS.getPathName();
        }

        private String getPathUpToTileEntity() {
            return VisibilityHandlersConfig.Factory.PATH + TileEntityVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull PacketEventsTileEntityVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            return new PacketEventsTileEntityVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsTileEntityVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
