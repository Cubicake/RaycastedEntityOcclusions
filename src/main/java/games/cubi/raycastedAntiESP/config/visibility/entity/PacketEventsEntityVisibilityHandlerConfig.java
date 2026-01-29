package games.cubi.raycastedAntiESP.config.visibility.entity;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.bukkit.configuration.file.FileConfiguration;
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
        public @NotNull PacketEventsEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config) {
            return new PacketEventsEntityVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
