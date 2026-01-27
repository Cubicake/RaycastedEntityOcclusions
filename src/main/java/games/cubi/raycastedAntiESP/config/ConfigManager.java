package games.cubi.raycastedAntiESP.config;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.config.raycast.EntityConfig;
import games.cubi.raycastedAntiESP.config.raycast.PlayerConfig;
import games.cubi.raycastedAntiESP.config.raycast.TileEntityConfig;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import games.cubi.raycastedAntiESP.config.engine.EngineConfig;
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
    private EngineConfig engineConfig;
    private VisibilityHandlersConfig visibilityHandlersConfig;

    private ConfigManager(RaycastedAntiESP plugin) {
        this.plugin = plugin;
        load();
    }

    public static ConfigManager initialiseConfigManager(RaycastedAntiESP plugin) {
        if (instance == null) {
            if (isNotOnMainThread()) {
                Logger.error(new RuntimeException("ConfigManager attempted to be initialised off the main thread. Please report this to the plugin developer."), 2);
                return null;
            }
            instance = new ConfigManager(plugin);
        }
        return instance;
    }
    public static ConfigManager get() {
        if (instance == null) Logger.errorAndReturn(new RuntimeException("ConfigManager accessed before being initiated. Please report this to the plugin developer."), 2);
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
        ConfigFactory<?>[] factories = setDefaults();

        // Load config objects
        playerConfig = ((PlayerConfig.Factory) factories[0]).getFromConfig(config, PlayerConfig.DEFAULT);
        entityConfig = ((EntityConfig.Factory) factories[1]).getFromConfig(config, EntityConfig.DEFAULT);
        tileEntityConfig = ((TileEntityConfig.Factory) factories[2]).getFromConfig(config, TileEntityConfig.DEFAULT);
        snapshotConfig = ((SnapshotConfig.Factory) factories[3]).getFromConfig(config, null);
        debugConfig = DebugConfig.getFromConfig(config, DebugConfig.DEFAULT);
        visibilityHandlersConfig = ((VisibilityHandlersConfig.Factory) factories[4]).getFromConfig(config, null);
        engineConfig = ((EngineConfig.Factory) factories[5]).getFromConfig(config, EngineConfig.DEFAULT);

        // Save any new defaults that were added
        plugin.saveConfig();

    }

    /**
     * Set default values in the configuration file if they don't exist
     */
    private ConfigFactory<?>[] setDefaults() {
        config.addDefault("config-version", "1.0");

        PlayerConfig.Factory playerFactory = new PlayerConfig.Factory();
        EntityConfig.Factory entityFactory = new EntityConfig.Factory();
        TileEntityConfig.Factory tileEntityFactory = new TileEntityConfig.Factory();
        SnapshotConfig.Factory snapshotFactory = new SnapshotConfig.Factory();
        VisibilityHandlersConfig.Factory visibilityFactory = new VisibilityHandlersConfig.Factory();
        EngineConfig.Factory engineFactory = new EngineConfig.Factory();

        ConfigFactory<?>[] factories = new ConfigFactory<?>[] {
            playerFactory,
            entityFactory,
            tileEntityFactory,
            snapshotFactory,
            visibilityFactory,
            engineFactory,
        };

        playerFactory.setDefaults(config, PlayerConfig.DEFAULT);
        entityFactory.setDefaults(config, EntityConfig.DEFAULT);
        tileEntityFactory.setDefaults(config, TileEntityConfig.DEFAULT);

        snapshotFactory.setDefaults(config, null);
        visibilityFactory.setDefaults(config, null);
        engineFactory.setDefaults(config, EngineConfig.DEFAULT);

        DebugConfig.setDefaults(config, DebugConfig.DEFAULT);

        config.options().copyDefaults(true);
        plugin.saveConfig();
        return factories;
    }

    /**
     * Update a single config value both in memory and in the file
     * @param path The config path (e.g., "player.enabled")
     * @param rawValue The raw string value to set
     * @return 1 for success, 0 for out of range, -1 for invalid input  TODO: This is currently broken, and even if it worked it doesn't handle setting lists
     */
    public int setConfigValue(String path, String rawValue) {
        if (!config.contains(path)) {
            return -1; // Path doesn't exist
        }

        Object currentValue = config.get(path);
        Object parsedValue = null;

        try {
            switch (currentValue) {
                case Boolean ignored -> {
                    String lower = rawValue.toLowerCase();
                    if (!lower.equals("true") && !lower.equals("false")) {
                        return -1;
                    }
                    parsedValue = Boolean.parseBoolean(lower);
                }
                case Integer ignored -> parsedValue = Integer.parseInt(rawValue);
                case Double ignored -> parsedValue = Double.parseDouble(rawValue);
                case String ignored -> {
                    //use ConfigEnum.getAllValues(); to validate that the string is an enum
                    String[] enums = ConfigEnum.getAllValues(); //they are already formatted correctly
                    boolean found = false;
                    for (String enumVal : enums) {
                        if (enumVal.equalsIgnoreCase(rawValue)) {
                            parsedValue = enumVal;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return -1;
                    }
                }
                case null, default -> {
                    return -1;
                }
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

    private static boolean isNotOnMainThread() {
        if (RaycastedAntiESP.get().getServer().isPrimaryThread()) {
            return false;
        }
        Logger.error(new RuntimeException("ConfigManager attempted to be accessed off the main thread. Please report this to the plugin developer."), 2);
        return true;
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

    public VisibilityHandlersConfig getVisibilityHandlersConfig() {
        return visibilityHandlersConfig;
    }

    public FileConfiguration getConfigFile() {
        return config;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }
}
