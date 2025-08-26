package games.cubi.raycastedAntiESP.config;


public abstract class RaycastConfig {
    private final byte engineMode;
    private final byte maxOccludingCount;
    private final short alwaysShowRadius;
    private final short raycastRadius;
    private final short visibleRecheckInterval;
    private final boolean enabled;

    public RaycastConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        this.engineMode = engineMode;
        this.maxOccludingCount = maxOccludingCount;
        this.alwaysShowRadius = alwaysShowRadius;
        this.raycastRadius = raycastRadius;
        this.visibleRecheckInterval = visibleRecheckInterval;
        this.enabled = enabled;
    }

    public RaycastConfig(int engineMode, int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled) {
        this.engineMode = (byte) engineMode;
        this.maxOccludingCount = (byte) maxOccludingCount;
        this.alwaysShowRadius = (short) alwaysShowRadius;
        this.raycastRadius = (short) raycastRadius;
        this.visibleRecheckInterval = (short) visibleRecheckInterval;
        this.enabled = enabled;
    }

    public RaycastConfig(boolean enabled) {
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

    public int getVisibleRecheckIntervalSeconds() {
        return  (visibleRecheckInterval * 20);
    }

    public boolean isEnabled() { return enabled; }
}
