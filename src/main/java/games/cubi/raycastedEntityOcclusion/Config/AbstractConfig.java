package games.cubi.raycastedEntityOcclusion.Config;


public class AbstractConfig {
    private final byte engineMode;
    private final byte maxOccludingCount;
    private final short alwaysShowRadius;
    private final short raycastRadius;
    private final short visibleRecheckInterval;
    private final boolean enabled;

    public AbstractConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        this.engineMode = engineMode;
        this.maxOccludingCount = maxOccludingCount;
        this.alwaysShowRadius = alwaysShowRadius;
        this.raycastRadius = raycastRadius;
        this.visibleRecheckInterval = visibleRecheckInterval;
        this.enabled = enabled;
    }
    public AbstractConfig(boolean enabled) {
        this.enabled = enabled;
        if (enabled) throw new IllegalArgumentException("Abstract config created without parameters while enabled");
        engineMode = -1; maxOccludingCount = -1; alwaysShowRadius = 48; raycastRadius = -1; visibleRecheckInterval = -1;

    }

    public byte getEngineMode() {
        return engineMode;
    }

    public byte getMaxOccludingCount() {
        return maxOccludingCount;
    }

    public short getAlwaysShowRadius() {
        return alwaysShowRadius;
    }

    public short getRaycastRadius() {
        return raycastRadius;
    }

    public short getVisibleRecheckInterval() {
        return visibleRecheckInterval;
    }

    public short getVisibleRecheckIntervalSeconds() {
        return (short) (visibleRecheckInterval * 20);
    }
}
