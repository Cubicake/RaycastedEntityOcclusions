package games.cubi.raycastedAntiESP.config.raycast;

import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityConfig extends RaycastConfig {
    public static final String PATH = "checks.entity";

    public static final EntityConfig DEFAULT = new EntityConfig(3, 16, 48, 50, true);

    public EntityConfig(byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public EntityConfig(int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public EntityConfig(boolean enabled) {
        super(enabled);
    }

    public EntityConfig(RaycastConfig superConfig) {
        super(superConfig);
    }

    public static class Factory extends RaycastConfig.Factory<EntityConfig> {
        public Factory() {
            super(PATH);
        }

        @Override
        public @NotNull EntityConfig getFromConfig(ConfigurationNode config) {
            return new EntityConfig(super.getFromConfig(config, DEFAULT));
        }

        @Override
        public @NotNull Factory setDefaults(ConfigurationNode config) {
            super.setDefaults(config, DEFAULT);
            return this;
        }
    }
}
