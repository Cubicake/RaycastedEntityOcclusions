package games.cubi.raycastedAntiESP.config.snapshot;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class SnapshotConfigFactory implements ConfigFactory<SnapshotConfig> {
    private static final String PATH = "snapshot";

    public SnapshotConfigFactory() {}

    public @NotNull SnapshotConfig getFromConfig(FileConfiguration config, SnapshotConfig defaults) {
        return new SnapshotConfig(
                config.getInt(PATH+".world-refresh-interval", defaults.getWorldSnapshotRefreshInterval()),
                config.getInt(PATH+".entity-location-refresh-interval", defaults.getEntityLocationRefreshInterval()),
                config.getBoolean(PATH+".perform-unsafe-async-world-snapshots", defaults.performUnsafeWorldSnapshots())
        );
    }

    public void setDefaults(FileConfiguration config, SnapshotConfig defaults) {
        config.addDefault(PATH+".world-refresh-interval", defaults.getWorldSnapshotRefreshInterval());
        config.addDefault(PATH+".entity-location-refresh-interval", defaults.getEntityLocationRefreshInterval());
        config.addDefault(PATH+".perform-unsafe-world-snapshots", defaults.performUnsafeWorldSnapshots());
    }
}
