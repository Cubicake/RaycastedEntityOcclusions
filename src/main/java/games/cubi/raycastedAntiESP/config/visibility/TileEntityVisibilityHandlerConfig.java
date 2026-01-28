package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        private final BukkitTileEntityVisibilityHandlerConfig.Factory bukkitFactory =
            new BukkitTileEntityVisibilityHandlerConfig.Factory(TileEntityVisibilityHandlerMode.BUKKIT);
        private final PacketEventsTileEntityVisibilityHandlerConfig.Factory packetEventsFactory =
            new PacketEventsTileEntityVisibilityHandlerConfig.Factory(TileEntityVisibilityHandlerMode.PACKETEVENTS);

        @Override
        public String getFullPath() {
            return VisibilityHandlersConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull TileEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                        @Nullable TileEntityVisibilityHandlerConfig defaults) {
            TileEntityVisibilityHandlerConfig fallback = defaults != null ? defaults : DEFAULT;
            TileEntityVisibilityHandlerMode mode = readMode(config, fallback);
            return switch (mode) {
                case BUKKIT -> bukkitFactory.getFromConfig(config, BukkitTileEntityVisibilityHandlerConfig.DEFAULT);
                case PACKETEVENTS -> packetEventsFactory.getFromConfig(
                    config,
                    PacketEventsTileEntityVisibilityHandlerConfig.DEFAULT
                );
            };
        }

        private TileEntityVisibilityHandlerMode readMode(FileConfiguration config, TileEntityVisibilityHandlerConfig fallback) {
            String modeName = config.getString(getFullPath() + ".mode", fallback.getName());
            TileEntityVisibilityHandlerMode mode = TileEntityVisibilityHandlerMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid tile entity visibility handler mode in config, defaulting to " + fallback.getName(), 3);
                mode = fallback.getMode();
            }
            return mode;
        }

        @Override
        public @NotNull ConfigFactory<TileEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                     @Nullable TileEntityVisibilityHandlerConfig defaults) {
            TileEntityVisibilityHandlerConfig fallback = defaults != null ? defaults : DEFAULT;
            config.addDefault(getFullPath() + ".mode", fallback.getName());
            TileEntityVisibilityHandlerMode mode = fallback.getMode();
            switch (mode) {
                case BUKKIT -> bukkitFactory.setDefaults(config, BukkitTileEntityVisibilityHandlerConfig.DEFAULT);
                case PACKETEVENTS -> packetEventsFactory.setDefaults(
                    config,
                    PacketEventsTileEntityVisibilityHandlerConfig.DEFAULT
                );
            }
            return this;
        }
    }
}
