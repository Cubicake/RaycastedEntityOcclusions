package games.cubi.raycastedAntiESP.config.raycast;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerConfig extends RaycastConfig {
    public static final String PATH = "checks.player";
    private final boolean onlyCullWhileSneaking;

    public static final PlayerConfig DEFAULT = new PlayerConfig(3, 16, 48, 50, true, true);

    public PlayerConfig(byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, boolean onlyCullWhileSneaking) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.onlyCullWhileSneaking = onlyCullWhileSneaking;
    }

    public PlayerConfig(int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled, boolean onlyCullWhileSneaking) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
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

    public static class Factory extends RaycastConfig.Factory<PlayerConfig>  {
        public Factory() {
            super(PATH);
        }

        @Override
        public @NotNull PlayerConfig getFromConfig(ConfigurationNode config) {
            return new PlayerConfig(
                    super.getFromConfig(config, DEFAULT),
                    ConfigNodeUtil.getBoolean(config, PATH+".only-cull-while-sneaking", DEFAULT.onlyCullWhileSneaking()));
        }

        @Override
        public @NotNull Factory setDefaults(ConfigurationNode config) {
            super.setDefaults(config, DEFAULT);
            ConfigNodeUtil.addDefault(config, PATH+".only-cull-while-sneaking", DEFAULT.onlyCullWhileSneaking());
            return this;
        }
    }
}
