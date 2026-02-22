package games.cubi.raycastedAntiESP.config.visibility.block;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class BukkitBlockVisibilityHandlerConfig extends BlockVisibilityHandlerConfig {
    protected BukkitBlockVisibilityHandlerConfig() {
        super(BlockVisibilityHandlerMode.BUKKIT);
    }

    public static final BukkitBlockVisibilityHandlerConfig DEFAULT = new BukkitBlockVisibilityHandlerConfig();

    static class Factory implements ConfigFactory<BukkitBlockVisibilityHandlerConfig> {

        Factory() {}

        @Override
        public String getFullPath() {
            return getPathUpToBlock() + BlockVisibilityHandlerMode.BUKKIT.getPathName();
        }

        private String getPathUpToBlock() {
            return VisibilityHandlersConfig.Factory.PATH + BlockVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull BukkitBlockVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            return new BukkitBlockVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<BukkitBlockVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
