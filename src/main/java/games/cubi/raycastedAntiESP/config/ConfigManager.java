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

    // Default config objects TODO: Keep these here or move into individual config classes?
    private static final PlayerConfig DEFAULT_PLAYER_CONFIG = new PlayerConfig(1, 3, 16, 48, 50, true, true);
    private static final EntityConfig DEFAULT_ENTITY_CONFIG = new EntityConfig( 1, 3, 16, 48, 50, true);
    private static final TileEntityConfig DEFAULT_TILE_ENTITY_CONFIG = new TileEntityConfig(1, 3, 16, 48, 0, true);
    private static final SnapshotConfig DEFAULT_SNAPSHOT_CONFIG = new SnapshotConfig(60, 0, false);
    private static final DebugConfig DEFAULT_DEBUG_CONFIG = new DebugConfig(3, 3, 3, false, false, false);

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
    public static ConfigManager get() {
        if (instance == null) Logger.errorAndReturn(new RuntimeException("ConfigManager accessed before being initiated. Please report this to the plugin developer."));
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
        snapshotConfig = SnapshotConfig.getFromConfig(config, getDefaultSnapshotConfig());
        debugConfig = DebugConfig.getFromConfig(config, getDefaultDebugConfig());

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

        SnapshotConfig.setDefaults(config, getDefaultSnapshotConfig());

        DebugConfig.setDefaults(config, getDefaultDebugConfig());

        config.options().copyDefaults(true);
        plugin.saveConfig();
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

    private static boolean isNotOnMainThread() {
        if (RaycastedAntiESP.get().getServer().isPrimaryThread()) {
            return false;
        }
        Logger.error(new RuntimeException("ConfigManager attempted to be accessed off the main thread. Please report this to the plugin developer."));
        return true;
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
}