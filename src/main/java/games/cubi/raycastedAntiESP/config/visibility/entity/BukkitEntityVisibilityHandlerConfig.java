package games.cubi.raycastedAntiESP.config.visibility.entity;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.bukkit.configuration.file.FileConfiguration;
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
        public @NotNull BukkitEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config) {
            return new BukkitEntityVisibilityHandlerConfig();
        }

        @Override
        public @NotNull ConfigFactory<BukkitEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
