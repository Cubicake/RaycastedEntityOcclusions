package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;

public class SnapshotConfig {
    private static final String PATH = "snapshot";
    private final short worldSnapshotRefreshInterval;
    private final short entityLocationRefreshInterval;
    private final boolean performUnsafeWorldSnapshots;

    public SnapshotConfig(short worldRefresh, short entityRefresh, boolean doUnsafeWorldSnapshots) {
        worldSnapshotRefreshInterval = worldRefresh;
        entityLocationRefreshInterval = entityRefresh;
        performUnsafeWorldSnapshots = doUnsafeWorldSnapshots;
    }

    public SnapshotConfig(int worldRefresh, int entityRefresh, boolean doUnsafeWorldSnapshots) {
        worldSnapshotRefreshInterval = (short) worldRefresh;
        entityLocationRefreshInterval = (short) entityRefresh;
        performUnsafeWorldSnapshots = doUnsafeWorldSnapshots;
    }

    public short getWorldSnapshotRefreshInterval() {
        return worldSnapshotRefreshInterval;
    }

    public short getEntityLocationRefreshInterval() {
        return entityLocationRefreshInterval;
    }

    public boolean performUnsafeWorldSnapshots() {
        return performUnsafeWorldSnapshots;
    }

    static SnapshotConfig getFromConfig(FileConfiguration config, SnapshotConfig defaults) {
        return new SnapshotConfig(
                config.getInt(PATH+".world-refresh-interval", defaults.getWorldSnapshotRefreshInterval()),
                config.getInt(PATH+".entity-location-refresh-interval", defaults.getEntityLocationRefreshInterval()),
                config.getBoolean(PATH+".perform-unsafe-async-world-snapshots", defaults.performUnsafeWorldSnapshots())
        );
    }

    static void setDefaults(FileConfiguration config, String path, RaycastConfig defaults) {
        config.addDefault(path+".enabled", defaults.isEnabled());
        config.addDefault(path+".engine-mode", defaults.getEngineMode());
        config.addDefault(path+".max-occluding-count", defaults.getMaxOccludingCount());
        config.addDefault(path+".always-show-radius", defaults.getAlwaysShowRadius());
        config.addDefault(path+".raycast-radius", defaults.getRaycastRadius());
        config.addDefault(path+".visible-recheck-interval", defaults.getVisibleRecheckInterval());
    }
}
