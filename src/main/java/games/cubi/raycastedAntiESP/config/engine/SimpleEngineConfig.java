package games.cubi.raycastedAntiESP.config.engine;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        public @NotNull SimpleEngineConfig getFromConfig(FileConfiguration config) {
            return new SimpleEngineConfig();
        }

        @Override
        public @NotNull ConfigFactory<SimpleEngineConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
