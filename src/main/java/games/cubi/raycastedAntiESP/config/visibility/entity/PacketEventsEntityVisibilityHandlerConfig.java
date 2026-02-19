package games.cubi.raycastedAntiESP.config.visibility.entity;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class PacketEventsEntityVisibilityHandlerConfig extends EntityVisibilityHandlerConfig {
    protected PacketEventsEntityVisibilityHandlerConfig() {
        super(EntityVisibilityHandlerMode.PACKETEVENTS);
    }

    public static final PacketEventsEntityVisibilityHandlerConfig DEFAULT = new PacketEventsEntityVisibilityHandlerConfig();

    static class Factory implements ConfigFactory<PacketEventsEntityVisibilityHandlerConfig> {

        Factory(EntityVisibilityHandlerMode mode) {}

        @Override
        public String getFullPath() {
            return getPathUpToEntity() + EntityVisibilityHandlerMode.PACKETEVENTS.getPathName();
        }

        private String getPathUpToEntity() {
            return VisibilityHandlersConfig.Factory.PATH + EntityVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull PacketEventsEntityVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            return new PacketEventsEntityVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsEntityVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
