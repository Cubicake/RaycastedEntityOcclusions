package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class PacketEventsEntityVisibilityHandlerConfig extends EntityVisibilityHandlerConfig {
    protected PacketEventsEntityVisibilityHandlerConfig(EntityVisibilityHandlerMode mode) {
        super(mode);
    }

    public static final PacketEventsEntityVisibilityHandlerConfig DEFAULT =
        new PacketEventsEntityVisibilityHandlerConfig(EntityVisibilityHandlerMode.PACKETEVENTS);

    static class Factory implements ConfigFactory<PacketEventsEntityVisibilityHandlerConfig> {
        private final EntityVisibilityHandlerMode mode;

        Factory(EntityVisibilityHandlerMode mode) {
            this.mode = mode;
        }

        @Override
        public String getFullPath() {
            return getPathUpToEntity() + mode.getPathName();
        }

        private String getPathUpToEntity() {
            return VisibilityHandlersConfig.Factory.PATH + EntityVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull PacketEventsEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                                PacketEventsEntityVisibilityHandlerConfig defaults) {
            return new PacketEventsEntityVisibilityHandlerConfig(mode);
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                              PacketEventsEntityVisibilityHandlerConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
