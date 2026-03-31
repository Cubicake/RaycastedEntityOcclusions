package games.cubi.raycastedantiesp.core.config.engine;

import games.cubi.logs.Frequency;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigFactory;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public abstract class EngineConfig implements Config {
    private final EngineMode mode;
    private static PredictiveEngineConfig.Factory predictiveFactory;
    private static SimpleEngineConfig.Factory simpleFactory;

    protected EngineConfig(EngineMode mode) {
        this.mode = mode;
    }

    public EngineMode getMode() {
        return mode;
    }
    public static final EngineConfig DEFAULT = new EngineConfig(EngineMode.SIMPLE) {};

    public static class Factory implements ConfigFactory<EngineConfig> {
        public static final String PATH = "engine";

        @Override
        public String getFullPath() {
            return PATH;
        }

        @Override
        public @NotNull EngineConfig getFromConfig(ConfigurationNode config) {
            String modeName = ConfigNodeUtil.getString(config, getFullPath() + ".mode", DEFAULT.getMode().getName());
            EngineMode mode = EngineMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid engine mode in config, defaulting to " + DEFAULT.getMode().getName(), Frequency.CONFIG_LOAD.value, EngineConfig.class);
                mode = DEFAULT.getMode();
            }
            return switch (mode) {
                case PREDICTIVE -> predictiveFactory().getFromConfig(config);
                case SIMPLE -> simpleFactory().getFromConfig(config);
            };
        }

        @Override
        public @NotNull ConfigFactory<EngineConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath() + ".mode", DEFAULT.getMode().getName());
            predictiveFactory().setDefaults(config);
            simpleFactory().setDefaults(config);
            return this;
        }
    }

    private static synchronized PredictiveEngineConfig.Factory predictiveFactory() {
        if (predictiveFactory == null) {
            predictiveFactory = new PredictiveEngineConfig.Factory();
        }
        return predictiveFactory;
    }

    private static synchronized SimpleEngineConfig.Factory simpleFactory() {
        if (simpleFactory == null) {
            simpleFactory = new SimpleEngineConfig.Factory();
        }
        return simpleFactory;
    }
}
