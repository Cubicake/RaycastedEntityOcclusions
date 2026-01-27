package games.cubi.raycastedAntiESP.config.raycast;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RaycastConfig implements Config {
    private static final RaycastConfig DISABLED_DEFAULT = new RaycastConfig(false);
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
        // This is unhinged but also the simplest way I can think of to do this
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

    public short getVisibleRecheckInterval() {
        return visibleRecheckInterval;
    }

    public int getVisibleRecheckIntervalSeconds() {
        return  (visibleRecheckInterval * 20);
    }

    public boolean isEnabled() { return enabled; }

    public static class Factory implements ConfigFactory<RaycastConfig> {
        private final String path;

        public Factory(String path) {
            this.path = path;
        }

        @Override
        public String getFullPath() {
            return path;
        }

        @Override
        public @NotNull RaycastConfig getFromConfig(FileConfiguration config, @Nullable RaycastConfig defaults) {
            RaycastConfig fallback = getFallback(defaults);
            return new RaycastConfig(
                    config.getInt(path+".max-occluding-count", fallback.getMaxOccludingCount()),
                    config.getInt(path+".always-show-radius", fallback.getAlwaysShowRadius()),
                    config.getInt(path+".raycast-radius", fallback.getRaycastRadius()),
                    config.getInt(path+".visible-recheck-interval", fallback.getVisibleRecheckInterval()),
                    config.getBoolean(path+".enabled", fallback.isEnabled())
            );
        }

        @Override
        public @NotNull ConfigFactory<RaycastConfig> setDefaults(FileConfiguration config, @Nullable RaycastConfig defaults) {
            RaycastConfig fallback = getFallback(defaults);
            config.addDefault(path+".enabled", fallback.isEnabled());
            config.addDefault(path+".max-occluding-count", fallback.getMaxOccludingCount());
            config.addDefault(path+".always-show-radius", fallback.getAlwaysShowRadius());
            config.addDefault(path+".raycast-radius", fallback.getRaycastRadius());
            config.addDefault(path+".visible-recheck-interval", fallback.getVisibleRecheckInterval());
            return this;
        }

        private RaycastConfig getFallback(@Nullable RaycastConfig defaults) {
            return defaults != null ? defaults : DISABLED_DEFAULT;
        }
    }
}
