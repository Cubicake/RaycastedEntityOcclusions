package games.cubi.raycastedAntiESP.config.visibility.block;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class PacketEventsBlockVisibilityHandlerConfig extends BlockVisibilityHandlerConfig {
    protected PacketEventsBlockVisibilityHandlerConfig() {
        super(BlockVisibilityHandlerMode.PACKETEVENTS);
    }

    public static final PacketEventsBlockVisibilityHandlerConfig DEFAULT =
        new PacketEventsBlockVisibilityHandlerConfig();

    static class Factory implements ConfigFactory<PacketEventsBlockVisibilityHandlerConfig> {

        Factory() {}

        @Override
        public String getFullPath() {
            return getPathUpToBlock() + BlockVisibilityHandlerMode.PACKETEVENTS.getPathName();
        }

        private String getPathUpToBlock() {
            return VisibilityHandlersConfig.Factory.PATH + BlockVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull PacketEventsBlockVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            return new PacketEventsBlockVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsBlockVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
