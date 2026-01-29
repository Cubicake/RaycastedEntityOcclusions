package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;
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
        public @NotNull DebugConfig getFromConfig(FileConfiguration config) {
            DebugConfig fallback = DEFAULT;
            return new DebugConfig(
                    (byte) config.getInt(PATH + ".info-level", fallback.getInfoLevel()),
                    (byte) config.getInt(PATH + ".warn-level", fallback.getWarnLevel()),
                    (byte) config.getInt(PATH + ".error-level", fallback.getErrorLevel()),
                    config.getBoolean(PATH + ".particles", fallback.showDebugParticles()),
                    config.getBoolean(PATH + ".timings", fallback.recordTimings()),
                    config.getBoolean(PATH + ".log-to-file", fallback.logToFile())
            );
        }

        @Override
        public @NotNull ConfigFactory<DebugConfig> setDefaults(FileConfiguration config) {
            DebugConfig fallback = DEFAULT;
            config.addDefault(PATH + ".info-level", fallback.getInfoLevel());
            config.addDefault(PATH + ".warn-level", fallback.getWarnLevel());
            config.addDefault(PATH + ".error-level", fallback.getErrorLevel());
            config.addDefault(PATH + ".particles", fallback.showDebugParticles());
            config.addDefault(PATH + ".timings", fallback.recordTimings());
            config.addDefault(PATH + ".log-to-file", fallback.logToFile());
            return this;
        }
    }
}
