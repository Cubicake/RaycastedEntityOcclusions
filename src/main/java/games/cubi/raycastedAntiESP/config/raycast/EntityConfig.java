package games.cubi.raycastedAntiESP.config.raycast;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityConfig extends RaycastConfig {
    public static final String PATH = "checks.entity";

    public static final EntityConfig DEFAULT = new EntityConfig(1, 1, 48, 10, true);

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

    public static class Factory extends RaycastConfig.Factory {
        public Factory() {
            super(PATH);
        }

        @Override
        public @NotNull EntityConfig getFromConfig(FileConfiguration config, @Nullable RaycastConfig defaults) {
            EntityConfig fallback = defaults instanceof EntityConfig entityDefaults ? entityDefaults : DEFAULT;
            return new EntityConfig(super.getFromConfig(config, fallback));
        }

        @Override
        public @NotNull RaycastConfig.Factory setDefaults(FileConfiguration config, @Nullable RaycastConfig defaults) {
            EntityConfig fallback = defaults instanceof EntityConfig entityDefaults ? entityDefaults : DEFAULT;
            super.setDefaults(config, fallback);
            return this;
        }
    }
}
