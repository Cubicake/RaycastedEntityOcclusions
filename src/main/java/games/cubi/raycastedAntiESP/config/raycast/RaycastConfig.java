package games.cubi.raycastedAntiESP.config.raycast;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaycastConfig implements Config {
    private static final RaycastConfig DEFAULTS = new RaycastConfig(false);
    private final byte maxOccludingCount;
    private final short alwaysShowRadius;
    private final short raycastRadius;
    private final short visibleRecheckInterval;
    private final boolean enabled;

    public RaycastConfig(byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        this.maxOccludingCount = maxOccludingCount;
        this.alwaysShowRadius = alwaysShowRadius;
        this.raycastRadius = raycastRadius;
        this.visibleRecheckInterval = visibleRecheckInterval;
        this.enabled = enabled;
    }

    public RaycastConfig(int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled) {
        this.maxOccludingCount = (byte) maxOccludingCount;
        this.alwaysShowRadius = (short) alwaysShowRadius;
        this.raycastRadius = (short) raycastRadius;
        this.visibleRecheckInterval = (short) visibleRecheckInterval;
        this.enabled = enabled;
    }

    public RaycastConfig(boolean enabled) {
        this.enabled = enabled;
        if (enabled) throw new IllegalArgumentException("Abstract config created without parameters while enabled");
        maxOccludingCount = -1; alwaysShowRadius = 48; raycastRadius = -1; visibleRecheckInterval = -1;

    }

    public RaycastConfig(RaycastConfig other) {
        this.maxOccludingCount = other.maxOccludingCount;
        this.alwaysShowRadius = other.alwaysShowRadius;
        this.raycastRadius = other.raycastRadius;
        this.visibleRecheckInterval = other.visibleRecheckInterval;
        this.enabled = other.enabled;
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

    protected short getRawVisibleRecheckInterval() {
        return visibleRecheckInterval;
    }

    public int getVisibleRecheckIntervalSeconds() {
        return  (visibleRecheckInterval * 20);
    }

    public boolean isEnabled() { return enabled; }

    public static abstract class Factory<T extends RaycastConfig> implements ConfigFactory<T> {
        private final String path;

        public Factory(String path) {
            this.path = path;
        }

        @Override
        public String getFullPath() {
            return path;
        }

        public @NotNull RaycastConfig getFromConfig(FileConfiguration config, @NotNull RaycastConfig defaults) {
            return new RaycastConfig(
                    config.getInt(path+".max-occluding-count", defaults.getMaxOccludingCount()),
                    config.getInt(path+".always-show-radius", defaults.getAlwaysShowRadius()),
                    config.getInt(path+".raycast-radius", defaults.getRaycastRadius()),
                    config.getInt(path+".visible-recheck-interval", defaults.getRawVisibleRecheckInterval()),
                    config.getBoolean(path+".enabled", defaults.isEnabled())
            );
        }

        public @NotNull ConfigFactory<T> setDefaults(FileConfiguration config, @Nullable RaycastConfig defaults) {
            RaycastConfig fallback = defaults != null ? defaults : DEFAULTS;
            config.addDefault(path+".enabled", fallback.isEnabled());
            config.addDefault(path+".max-occluding-count", fallback.getMaxOccludingCount());
            config.addDefault(path+".always-show-radius", fallback.getAlwaysShowRadius());
            config.addDefault(path+".raycast-radius", fallback.getRaycastRadius());
            config.addDefault(path+".visible-recheck-interval", fallback.getRawVisibleRecheckInterval());
            return this;
        }
    }
}
