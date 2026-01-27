package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BukkitBlockVisibilityHandlerConfig extends BlockVisibilityHandlerConfig {
    protected BukkitBlockVisibilityHandlerConfig(BlockVisibilityHandlerMode mode) {
        super(mode);
    }

    public static final BukkitBlockVisibilityHandlerConfig DEFAULT =
        new BukkitBlockVisibilityHandlerConfig(BlockVisibilityHandlerMode.BUKKIT);

    static class Factory implements ConfigFactory<BukkitBlockVisibilityHandlerConfig> {
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
        public @NotNull BukkitBlockVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                         BukkitBlockVisibilityHandlerConfig defaults) {
            return new BukkitBlockVisibilityHandlerConfig(mode);
        }

        @Override
        public @NotNull ConfigFactory<BukkitBlockVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                       BukkitBlockVisibilityHandlerConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
