package games.cubi.raycastedantiesp.core.config.snapshot.entity;

import games.cubi.logs.Frequency;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigFactory;
import games.cubi.raycastedantiesp.core.config.snapshot.SnapshotConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class EntitySnapshotConfig implements Config {
    private final EntityMode mode;

    public EntitySnapshotConfig(EntityMode mode) {
        this.mode = mode;
    }

    public EntityMode getMode() {
        return mode;
    }

    public static final EntitySnapshotConfig DEFAULT =
            new EntitySnapshotConfig(
                    EntityMode.PACKETEVENTS
            );

    public static class Factory implements ConfigFactory<EntitySnapshotConfig> {
        public final static String PATH = ".entity";
        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull EntitySnapshotConfig getFromConfig(ConfigurationNode config) {
            EntitySnapshotConfig fallback = DEFAULT;
            String modeName = ConfigNodeUtil.getString(config, getFullPath()+".mode", fallback.getMode().getName());
            EntityMode mode = EntityMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid entity snapshot mode in config, defaulting to " + fallback.getMode().getName(), Frequency.CONFIG_LOAD.value, EntitySnapshotConfig.class);
                mode = fallback.getMode();
            }
            if (mode != EntityMode.PACKETEVENTS) {
                Logger.warning("Entity snapshot mode '" + mode.getName() + "' is no longer supported, coercing to packetevents.", Frequency.CONFIG_LOAD.value, EntitySnapshotConfig.class);
                mode = EntityMode.PACKETEVENTS;
            }

            return switch (mode) {
                case PACKETEVENTS ->
                        new PacketEventsEntitySnapshotConfig.Factory().getFromConfig(config);
                default -> {
                    Logger.error(new RuntimeException("Unsupported entity snapshot mode enum value: " + mode + ", falling back on packetevents"), Frequency.CONFIG_LOAD.value, EntitySnapshotConfig.class);
                    yield new PacketEventsEntitySnapshotConfig.Factory().getFromConfig(config);
                }
            };
        }

        @Override
        public @NotNull ConfigFactory<EntitySnapshotConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath()+".mode", DEFAULT.getMode().getName());
            new BukkitEntitySnapshotConfig.Factory().setDefaults(config);
            new PacketEventsEntitySnapshotConfig.Factory().setDefaults(config);
            return this;
        }
    }
}
