package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;

public class PlayerConfig extends RaycastConfig {
    public static final String PATH = "player";
    private final boolean onlyCullWhileSneaking;

    public PlayerConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, boolean onlyCullWhileSneaking) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.onlyCullWhileSneaking = onlyCullWhileSneaking;
    }

    public PlayerConfig(int engineMode, int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled, boolean onlyCullWhileSneaking) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.onlyCullWhileSneaking = onlyCullWhileSneaking;
    }

    public PlayerConfig(boolean enabled) {
        super(enabled);
        this.onlyCullWhileSneaking = false;
    }

    public PlayerConfig(RaycastConfig superConfig, boolean onlyCullWhileSneaking) {
        super(superConfig);
        this.onlyCullWhileSneaking = onlyCullWhileSneaking;
    }

    public boolean onlyCullWhileSneaking() {
        return onlyCullWhileSneaking;
    }

    public static PlayerConfig getFromConfig(FileConfiguration config, PlayerConfig defaults) {
        return new PlayerConfig(RaycastConfig.getFromConfig(config, PATH, defaults), config.getBoolean(PATH+".only-cull-while-sneaking", defaults.onlyCullWhileSneaking()));
    }

    public static void setDefaults(FileConfiguration config, PlayerConfig defaults) {
        RaycastConfig.setDefaults(config, PATH, defaults);
        config.addDefault(PATH+".only-cull-while-sneaking", defaults.onlyCullWhileSneaking());
    }
}