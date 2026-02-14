package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;

public class RaycastConfig {
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

    public RaycastConfig(RaycastConfig other) {
        // This is unhinged but also the simplest way I can think of to do this
        this.engineMode = other.engineMode;
        this.maxOccludingCount = other.maxOccludingCount;
        this.alwaysShowRadius = other.alwaysShowRadius;
        this.raycastRadius = other.raycastRadius;
        this.visibleRecheckInterval = other.visibleRecheckInterval;
        this.enabled = other.enabled;
    }


    static RaycastConfig getFromConfig(FileConfiguration config, String path, RaycastConfig defaults) {
        return new RaycastConfig(
                config.getInt(path+".engine-mode", defaults.getEngineMode()),
                config.getInt(path+".max-occluding-count", defaults.getMaxOccludingCount()),
                config.getInt(path+".always-show-radius", defaults.getAlwaysShowRadius()),
                config.getInt(path+".raycast-radius", defaults.getRaycastRadius()),
                config.getInt(path+".visible-recheck-interval", defaults.getVisibleRecheckIntervalTicks()),
                config.getBoolean(path+".enabled", defaults.isEnabled())
        );
    }

    static void setDefaults(FileConfiguration config, String path, RaycastConfig defaults) {
        config.addDefault(path+".enabled", defaults.isEnabled());
        config.addDefault(path+".engine-mode", defaults.getEngineMode());
        config.addDefault(path+".max-occluding-count", defaults.getMaxOccludingCount());
        config.addDefault(path+".always-show-radius", defaults.getAlwaysShowRadius());
        config.addDefault(path+".raycast-radius", defaults.getRaycastRadius());
        config.addDefault(path+".visible-recheck-interval", defaults.getVisibleRecheckIntervalTicks());
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

    public short getVisibleRecheckIntervalTicks() {
        return visibleRecheckInterval;
    }

    protected short getVisibleRecheckIntervalRaw() {
        return visibleRecheckInterval;
    }

    public int getVisibleRecheckIntervalSeconds() {
        return  (visibleRecheckInterval * 20);
    }

    public boolean isEnabled() { return enabled; }
}
