package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockVisibilityHandlerConfig implements Config {
    private final BlockVisibilityHandlerMode mode;

    protected BlockVisibilityHandlerConfig(BlockVisibilityHandlerMode mode) {
        this.mode = mode;
    }

    public BlockVisibilityHandlerMode getMode() {
        return mode;
    }

    public String getName() {
        return mode.getName();
    }

    public String getPathName() {
        return mode.getPathName();
    }

    public static final BlockVisibilityHandlerConfig DEFAULT =
        new BlockVisibilityHandlerConfig(BlockVisibilityHandlerMode.BUKKIT);

    public static class Factory implements ConfigFactory<BlockVisibilityHandlerConfig> {
        public static final String PATH = ".block";
        private final BukkitBlockVisibilityHandlerConfig.Factory bukkitFactory =
            new BukkitBlockVisibilityHandlerConfig.Factory(BlockVisibilityHandlerMode.BUKKIT);
        private final PacketEventsBlockVisibilityHandlerConfig.Factory packetEventsFactory =
            new PacketEventsBlockVisibilityHandlerConfig.Factory(BlockVisibilityHandlerMode.PACKETEVENTS);

        @Override
        public String getFullPath() {
            return VisibilityHandlersConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull BlockVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                   @Nullable BlockVisibilityHandlerConfig defaults) {
            BlockVisibilityHandlerConfig fallback = defaults != null ? defaults : DEFAULT;
            BlockVisibilityHandlerMode mode = readMode(config, fallback);
            return switch (mode) {
                case BUKKIT -> bukkitFactory.getFromConfig(config, BukkitBlockVisibilityHandlerConfig.DEFAULT);
                case PACKETEVENTS -> packetEventsFactory.getFromConfig(
                    config,
                    PacketEventsBlockVisibilityHandlerConfig.DEFAULT
                );
            };
        }

        private BlockVisibilityHandlerMode readMode(FileConfiguration config, BlockVisibilityHandlerConfig fallback) {
            String modeName = config.getString(getFullPath() + ".mode", fallback.getName());
            BlockVisibilityHandlerMode mode = BlockVisibilityHandlerMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid block visibility handler mode in config, defaulting to " + fallback.getName(), 3);
                mode = fallback.getMode();
            }
            return mode;
        }

        @Override
        public @NotNull ConfigFactory<BlockVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                @Nullable BlockVisibilityHandlerConfig defaults) {
            BlockVisibilityHandlerConfig fallback = defaults != null ? defaults : DEFAULT;
            config.addDefault(getFullPath() + ".mode", fallback.getName());
            BlockVisibilityHandlerMode mode = fallback.getMode();
            switch (mode) {
                case BUKKIT -> bukkitFactory.setDefaults(config, BukkitBlockVisibilityHandlerConfig.DEFAULT);
                case PACKETEVENTS -> packetEventsFactory.setDefaults(
                    config,
                    PacketEventsBlockVisibilityHandlerConfig.DEFAULT
                );
            }
            return this;
        }
    }
}
