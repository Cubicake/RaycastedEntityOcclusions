package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;

public class EntityConfig extends RaycastConfig {
    public static final String PATH = "entity";

    public EntityConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public EntityConfig(int engineMode, int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public EntityConfig(boolean enabled) {
        super(enabled);
    }

    public EntityConfig(RaycastConfig superConfig) {
        super(superConfig);
    }

    public static EntityConfig getFromConfig(FileConfiguration config, EntityConfig defaults) {
        return new EntityConfig(RaycastConfig.getFromConfig(config, PATH, defaults));
    }

    public static void setDefaults(FileConfiguration config, EntityConfig defaults) {
        RaycastConfig.setDefaults(config, PATH, defaults);
    }
}
