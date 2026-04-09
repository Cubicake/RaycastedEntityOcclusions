package games.cubi.raycastedantiesp.core.config.visibility.block;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigFactory;
import games.cubi.raycastedantiesp.core.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

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
            new BukkitBlockVisibilityHandlerConfig.Factory();
        private final PacketEventsBlockVisibilityHandlerConfig.Factory packetEventsFactory =
            new PacketEventsBlockVisibilityHandlerConfig.Factory();

        @Override
        public String getFullPath() {
            return VisibilityHandlersConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull BlockVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            BlockVisibilityHandlerMode mode = readMode(config);
            return switch (mode) {
                case BUKKIT -> bukkitFactory.getFromConfig(config);
                case PACKETEVENTS -> packetEventsFactory.getFromConfig(config);
            };
        }

        private BlockVisibilityHandlerMode readMode(ConfigurationNode config) {
            String modeName = ConfigNodeUtil.getString(config, getFullPath() + ".mode", BlockVisibilityHandlerConfig.DEFAULT.getName());
            BlockVisibilityHandlerMode mode = BlockVisibilityHandlerMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid block visibility handler mode in config, defaulting to " + BlockVisibilityHandlerConfig.DEFAULT.getName(), 3, BlockVisibilityHandlerConfig.class);
                mode = BlockVisibilityHandlerConfig.DEFAULT.getMode();
            }
            return mode;
        }

        @Override
        public @NotNull ConfigFactory<BlockVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            BlockVisibilityHandlerConfig fallback = DEFAULT;
            ConfigNodeUtil.addDefault(config, getFullPath() + ".mode", fallback.getName());
            BlockVisibilityHandlerMode mode = fallback.getMode();
            switch (mode) {
                case BUKKIT -> bukkitFactory.setDefaults(config);
                case PACKETEVENTS -> packetEventsFactory.setDefaults(
                    config);
            }
            return this;
        }
    }
}
