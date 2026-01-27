package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BukkitEntityVisibilityHandlerConfig extends EntityVisibilityHandlerConfig {
    protected BukkitEntityVisibilityHandlerConfig(EntityVisibilityHandlerMode mode) {
        super(mode);
    }

    public static final BukkitEntityVisibilityHandlerConfig DEFAULT =
        new BukkitEntityVisibilityHandlerConfig(EntityVisibilityHandlerMode.BUKKIT);

    static class Factory implements ConfigFactory<BukkitEntityVisibilityHandlerConfig> {
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
        public @NotNull BukkitEntityVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                          BukkitEntityVisibilityHandlerConfig defaults) {
            return new BukkitEntityVisibilityHandlerConfig(mode);
        }

        @Override
        public @NotNull ConfigFactory<BukkitEntityVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                        BukkitEntityVisibilityHandlerConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
