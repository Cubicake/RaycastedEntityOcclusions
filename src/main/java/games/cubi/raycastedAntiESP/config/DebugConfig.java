package games.cubi.raycastedAntiESP.config;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DebugConfig implements Config {
    private static final String PATH = "debug";
    private static final Factory FACTORY = new Factory();

    private final byte infoLevel;
    private final byte warnLevel;
    private final byte errorLevel;
    private final boolean debugParticles;
    private final boolean timings;
    private final boolean logToFile;

    public DebugConfig(byte infoLevel, byte warnLevel, byte errorLevel, boolean debugParticles, boolean timings, boolean logToFile) {
        this.infoLevel = infoLevel;
        this.warnLevel = warnLevel;
        this.errorLevel = errorLevel;
        this.debugParticles = debugParticles;
        this.timings = timings;
        this.logToFile = logToFile;
    }

    public DebugConfig(int infoLevel, int warnLevel, int errorLevel, boolean debugParticles, boolean timings, boolean logToFile) {
        this.infoLevel = (byte) infoLevel;
        this.warnLevel = (byte) warnLevel;
        this.errorLevel = (byte) errorLevel;
        this.debugParticles = debugParticles;
        this.timings = timings;
        this.logToFile = logToFile;
    }

    public static final DebugConfig DEFAULT = new DebugConfig(5, 5, 5, false, false, false);

    public byte getInfoLevel() {
        return infoLevel;
    }

    public byte getWarnLevel() {
        return warnLevel;
    }

    public byte getErrorLevel() {
        return errorLevel;
    }

    public boolean showDebugParticles() {
        return debugParticles;
    }

    public boolean recordTimings() {
        return timings;
    }

    public boolean logToFile() {
        return logToFile;
    }

    public static class Factory implements ConfigFactory<DebugConfig> {
        @Override
        public String getFullPath() {
            return PATH;
        }

        @Override
        public @NotNull DebugConfig getFromConfig(ConfigurationNode config) {
            DebugConfig fallback = DEFAULT;
            return new DebugConfig(
                    (byte) ConfigNodeUtil.getInt(config, PATH + ".info-level", fallback.getInfoLevel()),
                    (byte) ConfigNodeUtil.getInt(config, PATH + ".warn-level", fallback.getWarnLevel()),
                    (byte) ConfigNodeUtil.getInt(config, PATH + ".error-level", fallback.getErrorLevel()),
                    ConfigNodeUtil.getBoolean(config, PATH + ".particles", fallback.showDebugParticles()),
                    ConfigNodeUtil.getBoolean(config, PATH + ".timings", fallback.recordTimings()),
                    ConfigNodeUtil.getBoolean(config, PATH + ".log-to-file", fallback.logToFile())
            );
        }

        @Override
        public @NotNull ConfigFactory<DebugConfig> setDefaults(ConfigurationNode config) {
            DebugConfig fallback = DEFAULT;
            ConfigNodeUtil.addDefault(config, PATH + ".info-level", fallback.getInfoLevel());
            ConfigNodeUtil.addDefault(config, PATH + ".warn-level", fallback.getWarnLevel());
            ConfigNodeUtil.addDefault(config, PATH + ".error-level", fallback.getErrorLevel());
            ConfigNodeUtil.addDefault(config, PATH + ".particles", fallback.showDebugParticles());
            ConfigNodeUtil.addDefault(config, PATH + ".timings", fallback.recordTimings());
            ConfigNodeUtil.addDefault(config, PATH + ".log-to-file", fallback.logToFile());
            return this;
        }
    }
}
