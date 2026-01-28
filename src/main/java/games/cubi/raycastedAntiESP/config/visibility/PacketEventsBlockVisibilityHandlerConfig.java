package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class PacketEventsBlockVisibilityHandlerConfig extends BlockVisibilityHandlerConfig {
    protected PacketEventsBlockVisibilityHandlerConfig(BlockVisibilityHandlerMode mode) {
        super(mode);
    }

    public static final PacketEventsBlockVisibilityHandlerConfig DEFAULT =
        new PacketEventsBlockVisibilityHandlerConfig(BlockVisibilityHandlerMode.PACKETEVENTS);

    static class Factory implements ConfigFactory<PacketEventsBlockVisibilityHandlerConfig> {
        private final BlockVisibilityHandlerMode mode;

        Factory(BlockVisibilityHandlerMode mode) {
            this.mode = mode;
        }

        @Override
        public String getFullPath() {
            return getPathUpToBlock() + mode.getPathName();
        }

        private String getPathUpToBlock() {
            return VisibilityHandlersConfig.Factory.PATH + BlockVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull PacketEventsBlockVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                               PacketEventsBlockVisibilityHandlerConfig defaults) {
            return new PacketEventsBlockVisibilityHandlerConfig(mode);
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsBlockVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                             PacketEventsBlockVisibilityHandlerConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
