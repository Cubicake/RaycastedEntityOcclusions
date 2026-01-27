package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;

public class DebugConfig {
    private static final String PATH = "debug";

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

    static DebugConfig getFromConfig(FileConfiguration config, DebugConfig defaults) {
        DebugConfig fallback = defaults != null ? defaults : DEFAULT;
        return new DebugConfig(
                (byte) config.getInt(PATH+".info-level", fallback.getInfoLevel()),
                (byte) config.getInt(PATH+".warn-level", fallback.getWarnLevel()),
                (byte) config.getInt(PATH+".error-level", fallback.getErrorLevel()),
                config.getBoolean(PATH+".particles", fallback.showDebugParticles()),
                config.getBoolean(PATH+".timings", fallback.recordTimings()),
                config.getBoolean(PATH+".log-to-file", fallback.logToFile())
        );
    }

    static void setDefaults(FileConfiguration config, DebugConfig defaults) {
        DebugConfig fallback = defaults != null ? defaults : DEFAULT;
        config.addDefault(PATH+".info-level", fallback.getInfoLevel());
        config.addDefault(PATH+".warn-level", fallback.getWarnLevel());
        config.addDefault(PATH+".error-level", fallback.getErrorLevel());
        config.addDefault(PATH+".particles", fallback.showDebugParticles());
        config.addDefault(PATH+".timings", fallback.recordTimings());
        config.addDefault(PATH+".log-to-file", fallback.logToFile());
    }
}
