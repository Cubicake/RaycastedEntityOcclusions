package games.cubi.raycastedantiesp.core.config.engine;

import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.ConfigFactory;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class SimpleEngineConfig extends EngineConfig {
    public SimpleEngineConfig() {
        super(EngineMode.SIMPLE);
    }

    public static final SimpleEngineConfig DEFAULT = new SimpleEngineConfig();

    public static class Factory implements ConfigFactory<SimpleEngineConfig> {
        @Override
        public String getFullPath() {
            return EngineConfig.Factory.PATH + EngineMode.SIMPLE.getPathName();
        }

        @Override
        public @NotNull SimpleEngineConfig getFromConfig(ConfigurationNode config) {
            return new SimpleEngineConfig();
        }

        @Override
        public @NotNull ConfigFactory<SimpleEngineConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
