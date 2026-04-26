package games.cubi.raycastedantiesp.core.config.raycast;

import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigLoadException;
import games.cubi.raycastedantiesp.core.config.ConfigReader;
import org.spongepowered.configurate.ConfigurationNode;

public class RaycastConfig implements Config {
    private final boolean enabled;
    private final boolean hideSoundsWhenHidden;
    private final byte maxOccludingCount;
    private final short alwaysShowRadius;
    private final short raycastRadius;
    private final short hideOnSpawnDistance;
    private final short visibleRecheckIntervalTicks;

    public RaycastConfig(boolean enabled, boolean hideSoundsWhenHidden, int maxOccludingCount, int alwaysShowRadius,
                         int raycastRadius, int hideOnSpawnDistance, int visibleRecheckIntervalTicks) {
        this.enabled = enabled;
        this.hideSoundsWhenHidden = hideSoundsWhenHidden;
        this.maxOccludingCount = (byte) maxOccludingCount;
        this.alwaysShowRadius = (short) alwaysShowRadius;
        this.raycastRadius = (short) raycastRadius;
        this.hideOnSpawnDistance = (short) hideOnSpawnDistance;
        this.visibleRecheckIntervalTicks = (short) visibleRecheckIntervalTicks;
    }

    protected static RaycastConfig load(ConfigurationNode node, String path, boolean hasHideSoundsWhenHidden) {
        int maxOccludingCount = ConfigReader.integer(ConfigReader.node(node, "max-occluding-count"), path + ".max-occluding-count");
        if (maxOccludingCount < 0 || maxOccludingCount > Byte.MAX_VALUE) {
            throw new ConfigLoadException(path + ".max-occluding-count must be between 0 and " + Byte.MAX_VALUE + " but was " + maxOccludingCount);
        }
        int alwaysShowRadius = ConfigReader.integer(ConfigReader.node(node, "always-show-radius"), path + ".always-show-radius");
        if (alwaysShowRadius < 0 || alwaysShowRadius > Short.MAX_VALUE) {
            throw new ConfigLoadException(path + ".always-show-radius must be between 0 and " + Short.MAX_VALUE + " but was " + alwaysShowRadius);
        }
        int raycastRadius = ConfigReader.integer(ConfigReader.node(node, "raycast-radius"), path + ".raycast-radius");
        if (raycastRadius < 0 || raycastRadius > Short.MAX_VALUE) {
            throw new ConfigLoadException(path + ".raycast-radius must be between 0 and " + Short.MAX_VALUE + " but was " + raycastRadius);
        }
        int hideOnSpawnDistance = ConfigReader.integer(ConfigReader.node(node, "hide-on-spawn-distance"), path + ".hide-on-spawn-distance");
        if (hideOnSpawnDistance < 0 || hideOnSpawnDistance > Short.MAX_VALUE) {
            throw new ConfigLoadException(path + ".hide-on-spawn-distance must be between 0 and " + Short.MAX_VALUE + " but was " + hideOnSpawnDistance);
        }
        int visibleRecheckIntervalTicks = ConfigReader.integer(ConfigReader.node(node, "visible-recheck-interval-ticks"), path + ".visible-recheck-interval-ticks");
        if (visibleRecheckIntervalTicks < 0 || visibleRecheckIntervalTicks > Short.MAX_VALUE) {
            throw new ConfigLoadException(path + ".visible-recheck-interval-ticks must be between 0 and " + Short.MAX_VALUE + " but was " + visibleRecheckIntervalTicks);
        }
        return new RaycastConfig(
                ConfigReader.bool(ConfigReader.node(node, "enabled"), path + ".enabled"),
                hasHideSoundsWhenHidden && ConfigReader.bool(ConfigReader.node(node, "hide-sounds-when-hidden"), path + ".hide-sounds-when-hidden"),
                maxOccludingCount,
                alwaysShowRadius,
                raycastRadius,
                hideOnSpawnDistance,
                visibleRecheckIntervalTicks
        );
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean hideSoundsWhenHidden() {
        return hideSoundsWhenHidden;
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

    public short hideOnSpawnDistance() {
        return hideOnSpawnDistance;
    }

    public short getVisibleRecheckIntervalTicks() {
        return visibleRecheckIntervalTicks;
    }
}
