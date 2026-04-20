package games.cubi.raycastedantiesp.core.config;

import games.cubi.logs.Frequency;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.raycast.EntityConfig;
import games.cubi.raycastedantiesp.core.config.raycast.PlatformTileEntityConfig;
import games.cubi.raycastedantiesp.core.config.raycast.PlayerConfig;
import games.cubi.raycastedantiesp.core.config.snapshot.SnapshotConfig;
import games.cubi.raycastedantiesp.core.config.visibility.VisibilityHandlersConfig;
import games.cubi.raycastedantiesp.core.config.engine.EngineConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
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

    private final Path configPath;
    private final YamlConfigurationLoader loader;
    private ConfigurationNode config;

    // Config objects
    private PlayerConfig playerConfig;
    private EntityConfig entityConfig;
    private PlatformTileEntityConfig<?> tileEntityConfig;
    private SnapshotConfig snapshotConfig;
    private DebugConfig debugConfig;
    private EngineConfig engineConfig;
    private VisibilityHandlersConfig visibilityHandlersConfig;

    // Platform provided
    public final InputStream resource;
    public final Path dataFolder;
    public final PlatformTileEntityConfig.Factory.FactoryProvider<?> tileEntityConfigFactoryProvider; // This is getting ridiculous lol


    private ConfigManager(InputStream resource, Path dataFolder, PlatformTileEntityConfig.Factory.FactoryProvider<?> tileEntityConfigFactoryProvider) {
        this.resource = resource;
        this.dataFolder = dataFolder;
        this.tileEntityConfigFactoryProvider = tileEntityConfigFactoryProvider;

        this.configPath = dataFolder.resolve("config.yml");
        this.loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        load();
    }

    public static ConfigManager initialiseConfigManager(InputStream resource, Path dataFolder, PlatformTileEntityConfig.Factory.FactoryProvider<?> tileEntityConfigFactoryProvider) {
        if (instance == null) {
            instance = new ConfigManager(resource, dataFolder, tileEntityConfigFactoryProvider);
        }
        return instance;
    }
    public static ConfigManager get() {
        if (instance == null) Logger.errorAndReturn(new RuntimeException("ConfigManager accessed before being initiated. Please report this to the plugin developer."), 2, ConfigManager.class);
        return instance;
    }

    /**
     * Load or reload the configuration from file
     */
    public void load() {
        ensureConfigFileExists();
        config = loadConfigNode();

        ConfigurationNode defaults = loadBundledDefaults();
        if (defaults != null) {
            mergeMissing(defaults, config);
        }

        // Set defaults if they don't exist
        ConfigFactory<?>[] factories = setDefaults();

        // Load config objects
        playerConfig = ((PlayerConfig.Factory) factories[0]).getFromConfig(config);
        entityConfig = ((EntityConfig.Factory) factories[1]).getFromConfig(config);
        tileEntityConfig = (PlatformTileEntityConfig<?>) factories[2].getFromConfig(config);
        snapshotConfig = ((SnapshotConfig.Factory) factories[3]).getFromConfig(config);
        debugConfig = ((DebugConfig.Factory) factories[4]).getFromConfig(config);
        visibilityHandlersConfig = ((VisibilityHandlersConfig.Factory) factories[5]).getFromConfig(config);
        engineConfig = ((EngineConfig.Factory) factories[6]).getFromConfig(config);

        // Persist the config tree after load/default initialization
        saveConfigNode();

    }

    /**
     * Set default values in the configuration file if they don't exist
     */
    private ConfigFactory<?>[] setDefaults() {
        ConfigNodeUtil.addDefault(config, "config-version", "1.0");

        PlayerConfig.Factory playerFactory = new PlayerConfig.Factory();
        EntityConfig.Factory entityFactory = new EntityConfig.Factory();
        PlatformTileEntityConfig.Factory<?,?> tileEntityFactory = tileEntityConfigFactoryProvider.getFactory();
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

    // Getters for current config objects
    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public EntityConfig getEntityConfig() {
        return entityConfig;
    }

    public PlatformTileEntityConfig<?> getTileEntityConfig() {
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
            Files.createDirectories(dataFolder);
            if (!Files.exists(configPath)) {
                if (resource != null) {
                    Files.copy(resource, configPath);
                } else {
                    Files.createFile(configPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to createPlayerEntitySnapshotManager config.yml", e);
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
        try {
            if (resource == null) return null;
            ConfigurationLoader<? extends ConfigurationNode> resourceLoader = YamlConfigurationLoader.builder()
                    .source(() -> new BufferedReader(new InputStreamReader(resource)))
                    .build();
            return resourceLoader.load();
        } catch (IOException e) {
            Logger.warning("Failed to read bundled config defaults: " + e.getMessage(), Frequency.CONFIG_LOAD.value, ConfigManager.class);
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
