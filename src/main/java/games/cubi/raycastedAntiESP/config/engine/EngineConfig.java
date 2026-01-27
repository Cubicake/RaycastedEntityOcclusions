package games.cubi.raycastedAntiESP.config.engine;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EngineConfig implements Config {
    private final EngineMode mode;
    private final int leniencyTicks;

    public EngineConfig(EngineMode mode, int leniencyTicks) {
        this.mode = mode;
        this.leniencyTicks = leniencyTicks;
    }

    public EngineMode getMode() {
        return mode;
    }

    public int getLeniencyTicks() {
        return leniencyTicks;
    }

    public static final EngineConfig DEFAULT = new EngineConfig(EngineMode.SIMPLE, 30);

    public static class Factory implements ConfigFactory<EngineConfig> {
        public static final String PATH = "engine";

        @Override
        public String getFullPath() {
            return PATH;
        }

        @Override
        public @NotNull EngineConfig getFromConfig(FileConfiguration config, @Nullable EngineConfig defaults) {
            EngineConfig fallback = defaults != null ? defaults : DEFAULT;
            String modeName = config.getString(getFullPath() + ".mode", fallback.getMode().getName());
            EngineMode mode = EngineMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid engine mode in config, defaulting to " + fallback.getMode().getName(), 3);
                mode = fallback.getMode();
            }

            int leniency = config.getInt(getFullPath() + EngineMode.PREDICTIVE.getPathName() + ".leniency", fallback.getLeniencyTicks());
            if (leniency < 0) {
                leniency = fallback.getLeniencyTicks();
            }
            return new EngineConfig(mode, leniency);
        }

        @Override
        public @NotNull ConfigFactory<EngineConfig> setDefaults(FileConfiguration config, @Nullable EngineConfig defaults) {
            EngineConfig fallback = defaults != null ? defaults : DEFAULT;
            config.addDefault(getFullPath() + ".mode", fallback.getMode().getName());
            config.addDefault(getFullPath() + EngineMode.PREDICTIVE.getPathName() + ".leniency", fallback.getLeniencyTicks());
            return this;
        }
    }
}
