package games.cubi.raycastedAntiESP.config.visibility.entity;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class BukkitEntityVisibilityHandlerConfig extends EntityVisibilityHandlerConfig {
    protected BukkitEntityVisibilityHandlerConfig() {
        super(EntityVisibilityHandlerMode.BUKKIT);
    }

    public static final BukkitEntityVisibilityHandlerConfig DEFAULT = new BukkitEntityVisibilityHandlerConfig();

    static class Factory implements ConfigFactory<BukkitEntityVisibilityHandlerConfig> {

        Factory(EntityVisibilityHandlerMode mode) {}

        @Override
        public String getFullPath() {
            return getPathUpToEntity() + EntityVisibilityHandlerMode.BUKKIT.getPathName();
        }

        private String getPathUpToEntity() {
            return VisibilityHandlersConfig.Factory.PATH + EntityVisibilityHandlerConfig.Factory.PATH;
        }

        @Override
        public @NotNull BukkitEntityVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            return new BukkitEntityVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<BukkitEntityVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
