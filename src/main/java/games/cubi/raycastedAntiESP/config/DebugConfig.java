package games.cubi.raycastedAntiESP.config;

public class DebugConfig {
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
}
