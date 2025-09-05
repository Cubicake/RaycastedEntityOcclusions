package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;

public class DebugConfig {
    private static final String PATH = "debug";
    private final byte infoLevel;
    private final byte warnLevel;
    private final byte errorLevel;
    private final boolean debugParticles;

    public DebugConfig(byte infoLevel, byte warnLevel, byte errorLevel, boolean debugParticles) {
        this.infoLevel = infoLevel;
        this.warnLevel = warnLevel;
        this.errorLevel = errorLevel;
        this.debugParticles = debugParticles;
    }

    public DebugConfig(int infoLevel, int warnLevel, int errorLevel, boolean debugParticles) {
        this.infoLevel = (byte) infoLevel;
        this.warnLevel = (byte) warnLevel;
        this.errorLevel = (byte) errorLevel;
        this.debugParticles = debugParticles;
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

    static DebugConfig getFromConfig(FileConfiguration config, DebugConfig defaults) {
        return new DebugConfig(
                (byte) config.getInt(PATH+".info-level", defaults.getInfoLevel()),
                (byte) config.getInt(PATH+".warn-level", defaults.getWarnLevel()),
                (byte) config.getInt(PATH+".error-level", defaults.getErrorLevel()),
                config.getBoolean(PATH+".particles", defaults.showDebugParticles())
        );
    }

    static void setDefaults(FileConfiguration config, DebugConfig defaults) {
        config.addDefault(PATH+".info-level", defaults.getInfoLevel());
        config.addDefault(PATH+".warn-level", defaults.getWarnLevel());
        config.addDefault(PATH+".error-level", defaults.getErrorLevel());
        config.addDefault(PATH+".particles", defaults.showDebugParticles());
    }
}
