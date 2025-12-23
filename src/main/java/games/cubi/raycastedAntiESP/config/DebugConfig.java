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
        return new DebugConfig(
                (byte) config.getInt(PATH+".info-level", defaults.getInfoLevel()),
                (byte) config.getInt(PATH+".warn-level", defaults.getWarnLevel()),
                (byte) config.getInt(PATH+".error-level", defaults.getErrorLevel()),
                config.getBoolean(PATH+".particles", defaults.showDebugParticles()),
                config.getBoolean(PATH+".timings", defaults.recordTimings()),
                config.getBoolean(PATH+".log-to-file", defaults.logToFile())
        );
    }

    static void setDefaults(FileConfiguration config, DebugConfig defaults) {
        config.addDefault(PATH+".info-level", defaults.getInfoLevel());
        config.addDefault(PATH+".warn-level", defaults.getWarnLevel());
        config.addDefault(PATH+".error-level", defaults.getErrorLevel());
        config.addDefault(PATH+".particles", defaults.showDebugParticles());
        config.addDefault(PATH+".timings", defaults.recordTimings());
        config.addDefault(PATH+".log-to-file", defaults.logToFile());
    }
}
