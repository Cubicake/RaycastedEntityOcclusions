package games.cubi.raycastedAntiESP.config.engine;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PredictiveEngineConfig extends EngineConfig {
    private final int leniencyTicks;

    public PredictiveEngineConfig(int leniencyTicks) {
        super(EngineMode.PREDICTIVE);
        this.leniencyTicks = leniencyTicks;
        Logger.warning(new RuntimeException("Predictive Engine not functional"), Logger.Frequency.CONFIG_LOAD.value);
    }

    public int getLeniencyTicks() {
        return leniencyTicks;
    }

    public static final PredictiveEngineConfig DEFAULT = new PredictiveEngineConfig(30);

    public static class Factory implements ConfigFactory<PredictiveEngineConfig> {
        @Override
        public String getFullPath() {
            return EngineConfig.Factory.PATH + EngineMode.PREDICTIVE.getPathName();
        }

        @Override
        public @NotNull PredictiveEngineConfig getFromConfig(FileConfiguration config) {
            int leniency = config.getInt(getFullPath() + ".leniency", DEFAULT.getLeniencyTicks());
            if (leniency < 0) {
                leniency = DEFAULT.getLeniencyTicks();
            }
            return new PredictiveEngineConfig(leniency);
        }

        @Override
        public @NotNull ConfigFactory<PredictiveEngineConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getFullPath() + ".leniency", DEFAULT.getLeniencyTicks());
            return this;
        }
    }
}
