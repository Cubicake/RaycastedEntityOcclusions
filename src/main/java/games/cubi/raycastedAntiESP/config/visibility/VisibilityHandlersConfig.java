package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VisibilityHandlersConfig implements Config {
    private final VisibilityHandlerMode blockMode;
    private final VisibilityHandlerMode entityMode;
    private final VisibilityHandlerMode tileEntityMode;

    public VisibilityHandlersConfig(VisibilityHandlerMode blockMode, VisibilityHandlerMode entityMode, VisibilityHandlerMode tileEntityMode) {
        this.blockMode = blockMode;
        this.entityMode = entityMode;
        this.tileEntityMode = tileEntityMode;
    }

    public VisibilityHandlerMode getBlockMode() {
        return blockMode;
    }

    public VisibilityHandlerMode getEntityMode() {
        return entityMode;
    }

    public VisibilityHandlerMode getTileEntityMode() {
        return tileEntityMode;
    }

    public static final VisibilityHandlersConfig DEFAULT =
            new VisibilityHandlersConfig(VisibilityHandlerMode.BUKKIT, VisibilityHandlerMode.BUKKIT, VisibilityHandlerMode.BUKKIT);

    public static class Factory implements ConfigFactory<VisibilityHandlersConfig> {
        public static final String PATH = "visibility-handlers";

        @Override
        public String getFullPath() {
            return PATH;
        }

        @Override
        public @NotNull VisibilityHandlersConfig getFromConfig(FileConfiguration config, @Nullable VisibilityHandlersConfig defaults) {
            VisibilityHandlersConfig fallback = defaults != null ? defaults : DEFAULT;
            VisibilityHandlerMode block = readMode(config, ".block", fallback.getBlockMode());
            VisibilityHandlerMode entity = readMode(config, ".entity", fallback.getEntityMode());
            VisibilityHandlerMode tileEntity = readMode(config, ".tile-entity", fallback.getTileEntityMode());
            return new VisibilityHandlersConfig(block, entity, tileEntity);
        }

        private VisibilityHandlerMode readMode(FileConfiguration config, String suffix, VisibilityHandlerMode fallback) {
            String modeName = config.getString(getFullPath() + suffix + ".mode", fallback.getName());
            VisibilityHandlerMode mode = VisibilityHandlerMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid visibility handler mode in config, defaulting to " + fallback.getName(), 3);
                mode = fallback;
            }
            return mode;
        }

        @Override
        public @NotNull ConfigFactory<VisibilityHandlersConfig> setDefaults(FileConfiguration config, @Nullable VisibilityHandlersConfig defaults) {
            VisibilityHandlersConfig fallback = defaults != null ? defaults : DEFAULT;
            config.addDefault(getFullPath() + ".block.mode", fallback.getBlockMode().getName());
            config.addDefault(getFullPath() + ".entity.mode", fallback.getEntityMode().getName());
            config.addDefault(getFullPath() + ".tile-entity.mode", fallback.getTileEntityMode().getName());
            return this;
        }
    }
}
