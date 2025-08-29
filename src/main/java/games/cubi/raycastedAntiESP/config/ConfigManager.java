package games.cubi.raycastedAntiESP.config;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private static ConfigManager instance;

    private final RaycastedAntiESP plugin;
    private FileConfiguration config;

    // Config objects
    private PlayerConfig playerConfig;
    private EntityConfig entityConfig;
    private TileEntityConfig tileEntityConfig;
    private SnapshotConfig snapshotConfig;
    private DebugConfig debugConfig;

    // Default config objects
    private static final PlayerConfig DEFAULT_PLAYER_CONFIG = new PlayerConfig(1, 3, 16, 48, 50, true, true);
    private static final EntityConfig DEFAULT_ENTITY_CONFIG = new EntityConfig( 1, 3, 16, 48, 50, true);
    private static final TileEntityConfig DEFAULT_TILE_ENTITY_CONFIG = new TileEntityConfig(1, 3, 16, 48, 0, true);
    private static final SnapshotConfig DEFAULT_SNAPSHOT_CONFIG = new SnapshotConfig(60, 60, false);
    private static final DebugConfig DEFAULT_DEBUG_CONFIG = new DebugConfig(1, 2, 2, false);

    private int maxEngineMode;

    private ConfigManager(RaycastedAntiESP plugin) {
        this.plugin = plugin;
        load();
    }

    public static ConfigManager initiateConfigManager(RaycastedAntiESP plugin) {
        if (instance == null) {
            if (isNotOnMainThread()) return null;
            instance = new ConfigManager(plugin);
        }
        return instance;
    }

    /**
     * Load or reload the configuration from file
     */
    public void load() {
        //check that we are on the main bukkit thread to prevent concurrency issues
        if (isNotOnMainThread()) return;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        // Set defaults if they don't exist
        setDefaults();

        // Load config objects
        playerConfig = PlayerConfig.getFromConfig(config, getDefaultPlayerConfig());
        entityConfig = EntityConfig.getFromConfig(config, getDefaultEntityConfig());
        tileEntityConfig = TileEntityConfig.getFromConfig(config, getDefaultTileEntityConfig());
        loadSnapshotConfig();
        loadDebugConfig();

        // Save any new defaults that were added
        plugin.saveConfig();

        maxEngineMode = calculateMaxEngineMode();
    }

    /**
     * Set default values in the configuration file if they don't exist
     */
    private void setDefaults() {
        config.addDefault("config-version", "1.0");

        PlayerConfig.setDefaults(config, getDefaultPlayerConfig());
        EntityConfig.setDefaults(config, getDefaultEntityConfig());
        TileEntityConfig.setDefaults(config, getTileEntityConfig());

        // Snapshot defaults
        config.addDefault("snapshot.world-refresh-interval", DEFAULT_SNAPSHOT_CONFIG.getWorldSnapshotRefreshInterval());
        config.addDefault("snapshot.entity-location-refresh-interval", DEFAULT_SNAPSHOT_CONFIG.getEntityLocationRefreshInterval());
        config.addDefault("snapshot.perform-unsafe-world-snapshots", DEFAULT_SNAPSHOT_CONFIG.performUnsafeWorldSnapshots());

        // Debug defaults
        config.addDefault("debug.info-level", DEFAULT_DEBUG_CONFIG.getInfoLevel());
        config.addDefault("debug.warn-level", DEFAULT_DEBUG_CONFIG.getWarnLevel());
        config.addDefault("debug.error-level", DEFAULT_DEBUG_CONFIG.getErrorLevel());
        config.addDefault("debug.particles", DEFAULT_DEBUG_CONFIG.showDebugParticles());

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    /**
     * Load snapshot configuration
     */
    private void loadSnapshotConfig() {
        snapshotConfig = new SnapshotConfig(
                (short) config.getInt("snapshot.world-refresh-interval", DEFAULT_SNAPSHOT_CONFIG.getWorldSnapshotRefreshInterval()),
                (short) config.getInt("snapshot.entity-location-refresh-interval", DEFAULT_SNAPSHOT_CONFIG.getEntityLocationRefreshInterval()),
                config.getBoolean("snapshot.perform-unsafe-world-snapshots", DEFAULT_SNAPSHOT_CONFIG.performUnsafeWorldSnapshots())
        );
    }

    /**
     * Load debug configuration
     */
    private void loadDebugConfig() {
        debugConfig = new DebugConfig(
                (byte) config.getInt("debug.info-level", DEFAULT_DEBUG_CONFIG.getInfoLevel()),
                (byte) config.getInt("debug.warn-level", DEFAULT_DEBUG_CONFIG.getWarnLevel()),
                (byte) config.getInt("debug.error-level", DEFAULT_DEBUG_CONFIG.getErrorLevel()),
                config.getBoolean("debug.particles", DEFAULT_DEBUG_CONFIG.showDebugParticles())
        );
    }

    /**
     * Update a single config value both in memory and in the file
     * @param path The config path (e.g., "player.enabled")
     * @param rawValue The raw string value to set
     * @return 1 for success, 0 for out of range, -1 for invalid input
     */
    public int setConfigValue(String path, String rawValue) {
        if (!config.contains(path)) {
            return -1; // Path doesn't exist
        }

        Object currentValue = config.get(path);
        Object parsedValue;

        try {
            if (currentValue instanceof Boolean) {
                String lower = rawValue.toLowerCase();
                if (!lower.equals("true") && !lower.equals("false")) {
                    return -1;
                }
                parsedValue = Boolean.parseBoolean(lower);
            } else if (currentValue instanceof Integer) {
                int intVal = Integer.parseInt(rawValue);
                if (intVal < 0 || intVal > 255) {
                    return 0;
                }
                parsedValue = intVal;
            } else {
                return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }

        // Update the config file
        config.set(path, parsedValue);
        plugin.saveConfig();

        // Reload to update the config objects
        load();

        return 1;
    }

    private int calculateMaxEngineMode() {
        //return the highest engine mode listed
        int maxMode = 0;
        if (playerConfig.getEngineMode() > maxMode) maxMode = playerConfig.getEngineMode();
        if (entityConfig.getEngineMode() > maxMode) maxMode = entityConfig.getEngineMode();
        if (tileEntityConfig.getEngineMode() > maxMode) maxMode = tileEntityConfig.getEngineMode();
        return maxMode;
    }


    public static PlayerConfig getDefaultPlayerConfig() {
        return DEFAULT_PLAYER_CONFIG;
    }

    public static EntityConfig getDefaultEntityConfig() {
        return DEFAULT_ENTITY_CONFIG;
    }

    public static TileEntityConfig getDefaultTileEntityConfig() {
        return DEFAULT_TILE_ENTITY_CONFIG;
    }

    public static SnapshotConfig getDefaultSnapshotConfig() {
        return DEFAULT_SNAPSHOT_CONFIG;
    }

    public static DebugConfig getDefaultDebugConfig() {
        return DEFAULT_DEBUG_CONFIG;
    }

    // Getters for current config objects
    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public EntityConfig getEntityConfig() {
        return entityConfig;
    }

    public TileEntityConfig getTileEntityConfig() {
        return tileEntityConfig;
    }

    public SnapshotConfig getSnapshotConfig() {
        return snapshotConfig;
    }

    public DebugConfig getDebugConfig() {
        return debugConfig;
    }

    public FileConfiguration getConfigFile() {
        return config;
    }

    public int getEngineMode() {
        return maxEngineMode;
    }

    private static boolean isNotOnMainThread() {
        if (RaycastedAntiESP.get().getServer().isPrimaryThread()) {
            return false;
        }
        Logger.error(new RuntimeException("ConfigManager attempted to be accessed off the main thread. Please report this to the plugin developer."));
        return true;
    }
}