package games.cubi.raycastedantiesp.core.config.visibility.tileentity;

import games.cubi.raycastedantiesp.core.Logger;
import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigFactory;
import games.cubi.raycastedantiesp.core.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class TileEntityVisibilityHandlerConfig implements Config {
    private final TileEntityVisibilityHandlerMode mode;

    protected TileEntityVisibilityHandlerConfig(TileEntityVisibilityHandlerMode mode) {
        this.mode = mode;
    }

    public TileEntityVisibilityHandlerMode getMode() {
        return mode;
    }

    public String getName() {
        return mode.getName();
    }

    public String getPathName() {
        return mode.getPathName();
    }

    public static final TileEntityVisibilityHandlerConfig DEFAULT =
        new TileEntityVisibilityHandlerConfig(TileEntityVisibilityHandlerMode.BUKKIT);

    public static class Factory implements ConfigFactory<TileEntityVisibilityHandlerConfig> {
        public static final String PATH = ".tile-entity";
        private final BukkitTileEntityVisibilityHandlerConfig.Factory bukkitFactory = new BukkitTileEntityVisibilityHandlerConfig.Factory();
        private final PacketEventsTileEntityVisibilityHandlerConfig.Factory packetEventsFactory = new PacketEventsTileEntityVisibilityHandlerConfig.Factory();

        @Override
        public String getFullPath() {
            return VisibilityHandlersConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull TileEntityVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            TileEntityVisibilityHandlerMode mode = readMode(config);
            return switch (mode) {
                case BUKKIT -> bukkitFactory.getFromConfig(config);
                case PACKETEVENTS -> packetEventsFactory.getFromConfig(config);
            };
        }

        private TileEntityVisibilityHandlerMode readMode(ConfigurationNode config) {
            String modeName = ConfigNodeUtil.getString(config, getFullPath() + ".mode", TileEntityVisibilityHandlerConfig.DEFAULT.getName());
            TileEntityVisibilityHandlerMode mode = TileEntityVisibilityHandlerMode.fromString(modeName);
            if (mode == null) {
                Logger.get().warning("Invalid tile entity visibility handler mode in config, defaulting to " + TileEntityVisibilityHandlerConfig.DEFAULT.getName(), 3);
                mode = TileEntityVisibilityHandlerConfig.DEFAULT.getMode();
            }
            return mode;
        }

        @Override
        public @NotNull ConfigFactory<TileEntityVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            TileEntityVisibilityHandlerConfig fallback = DEFAULT;
            ConfigNodeUtil.addDefault(config, getFullPath() + ".mode", fallback.getName());
            TileEntityVisibilityHandlerMode mode = fallback.getMode();
            switch (mode) {
                case BUKKIT -> bukkitFactory.setDefaults(config);
                case PACKETEVENTS -> packetEventsFactory.setDefaults(config);
            }
            return this;
        }
    }
}
