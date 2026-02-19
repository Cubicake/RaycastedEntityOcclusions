package games.cubi.raycastedAntiESP.config;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.config.raycast.EntityConfig;
import games.cubi.raycastedAntiESP.config.raycast.PlayerConfig;
import games.cubi.raycastedAntiESP.config.raycast.TileEntityConfig;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import games.cubi.raycastedAntiESP.config.engine.EngineConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {
    private static ConfigManager instance;

    private final RaycastedAntiESP plugin;
    private final Path configPath;
    private final YamlConfigurationLoader loader;
    private ConfigurationNode config;

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
        this.configPath = plugin.getDataFolder().toPath().resolve("config.yml");
        this.loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .build();
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
        ensureConfigFileExists();
        config = loadConfigNode();

        ConfigurationNode defaults = loadBundledDefaults();
        if (defaults != null) {
            mergeMissing(defaults, config);
        }
        if (!ConfigNodeUtil.contains(config, "configurate-migrated")) {
            Logger.info("Migrated configuration loading to Configurate YAML.", Logger.Frequency.CONFIG_LOAD.value);
            ConfigNodeUtil.set(config, "configurate-migrated", true);
        }

        // Set defaults if they don't exist
        ConfigFactory<?>[] factories = setDefaults();

        // Load config objects
        playerConfig = ((PlayerConfig.Factory) factories[0]).getFromConfig(config);
        entityConfig = ((EntityConfig.Factory) factories[1]).getFromConfig(config);
        tileEntityConfig = ((TileEntityConfig.Factory) factories[2]).getFromConfig(config);
        snapshotConfig = ((SnapshotConfig.Factory) factories[3]).getFromConfig(config);
        debugConfig = ((DebugConfig.Factory) factories[4]).getFromConfig(config);
        visibilityHandlersConfig = ((VisibilityHandlersConfig.Factory) factories[5]).getFromConfig(config);
        engineConfig = ((EngineConfig.Factory) factories[6]).getFromConfig(config);

        // Save any new defaults that were added
        saveConfigNode();

    }

    /**
     * Set default values in the configuration file if they don't exist
     */
    private ConfigFactory<?>[] setDefaults() {
        ConfigNodeUtil.addDefault(config, "config-version", "1.0");

        PlayerConfig.Factory playerFactory = new PlayerConfig.Factory();
        EntityConfig.Factory entityFactory = new EntityConfig.Factory();
        TileEntityConfig.Factory tileEntityFactory = new TileEntityConfig.Factory();
        SnapshotConfig.Factory snapshotFactory = new SnapshotConfig.Factory();
        DebugConfig.Factory debugFactory = new DebugConfig.Factory();
        VisibilityHandlersConfig.Factory visibilityFactory = new VisibilityHandlersConfig.Factory();
        EngineConfig.Factory engineFactory = new EngineConfig.Factory();

        ConfigFactory<?>[] factories = new ConfigFactory<?>[] {
            playerFactory,
            entityFactory,
            tileEntityFactory,
            snapshotFactory,
            debugFactory,
            visibilityFactory,
            engineFactory,
        };

        playerFactory.setDefaults(config);
        entityFactory.setDefaults(config);
        tileEntityFactory.setDefaults(config);

        snapshotFactory.setDefaults(config);
        debugFactory.setDefaults(config);
        visibilityFactory.setDefaults(config);
        engineFactory.setDefaults(config);

        return factories;
    }

    /**
     * Update a single config value both in memory and in the file
     * @param path The config path (e.g., "player.enabled")
     * @param rawValue The raw string value to set
     * @return 1 for success, 0 for out of range, -1 for invalid input  TODO: This is currently broken, and even if it worked it doesn't handle setting lists
     */
    public int setConfigValue(String path, String rawValue) {
        if (!ConfigNodeUtil.contains(config, path)) {
            return -1; // Path doesn't exist
        }

        Object currentValue = ConfigNodeUtil.get(config, path);
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
        ConfigNodeUtil.set(config, path, parsedValue);
        saveConfigNode();

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

    public ConfigurationNode getConfigFile() {
        return config;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }

    public Map<String, Object> getConfigValues() {
        Map<String, Object> values = new LinkedHashMap<>();
        collectConfigValues(config, "", values);
        return values;
    }

    private void collectConfigValues(ConfigurationNode node, String path, Map<String, Object> values) {
        if (node.childrenMap().isEmpty()) {
            if (!path.isEmpty() && !node.virtual()) {
                values.put(path, node.raw());
            }
            return;
        }

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            String key = String.valueOf(entry.getKey());
            String nextPath = path.isEmpty() ? key : path + "." + key;
            collectConfigValues(entry.getValue(), nextPath, values);
        }
    }

    private void ensureConfigFileExists() {
        try {
            Files.createDirectories(plugin.getDataFolder().toPath());
            if (!Files.exists(configPath)) {
                try (InputStream resource = plugin.getResource("config.yml")) {
                    if (resource != null) {
                        Files.copy(resource, configPath);
                    } else {
                        Files.createFile(configPath);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create config.yml", e);
        }
    }

    private ConfigurationNode loadConfigNode() {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.yml", e);
        }
    }

    private void saveConfigNode() {
        try {
            loader.save(config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config.yml", e);
        }
    }

    private ConfigurationNode loadBundledDefaults() {
        try (InputStream resource = plugin.getResource("config.yml")) {
            if (resource == null) return null;
            ConfigurationLoader<? extends ConfigurationNode> resourceLoader = YamlConfigurationLoader.builder()
                    .source(() -> new BufferedReader(new InputStreamReader(resource)))
                    .build();
            return resourceLoader.load();
        } catch (IOException e) {
            Logger.warning("Failed to read bundled config defaults: " + e.getMessage(), Logger.Frequency.CONFIG_LOAD.value);
            return null;
        }
    }

    private void mergeMissing(ConfigurationNode defaults, ConfigurationNode target) {
        if (defaults.childrenMap().isEmpty()) {
            if (target.virtual() && defaults.raw() != null) {
                try {
                    target.set(defaults.raw());
                } catch (SerializationException e) {
                    throw new RuntimeException("Failed to merge config defaults", e);
                }
            }
            return;
        }

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : defaults.childrenMap().entrySet()) {
            mergeMissing(entry.getValue(), target.node(entry.getKey()));
        }
    }
}
