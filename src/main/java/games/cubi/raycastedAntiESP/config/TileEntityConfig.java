package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;

public class TileEntityConfig extends RaycastConfig {
    public static final String PATH = "tile-entity";

    public TileEntityConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public TileEntityConfig(int engineMode, int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public TileEntityConfig(boolean enabled) {
        super(enabled);
    }

    public TileEntityConfig(RaycastConfig superConfig) {
        super(superConfig);
    }

    @Override
    public int getVisibleRecheckIntervalSeconds() {
        return super.getVisibleRecheckInterval();
    }

    public static TileEntityConfig getFromConfig(FileConfiguration config, TileEntityConfig defaults) {
        return new TileEntityConfig(RaycastConfig.getFromConfig(config, PATH, defaults));
    }

    public static void setDefaults(FileConfiguration config, TileEntityConfig defaults) {
        RaycastConfig.setDefaults(config, PATH, defaults);
    }
}
