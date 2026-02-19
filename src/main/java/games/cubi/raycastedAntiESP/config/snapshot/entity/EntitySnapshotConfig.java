package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
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
                    EntityMode.BUKKIT
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
                Logger.warning("Invalid entity snapshot mode in config, defaulting to " + fallback.getMode().getName(), Logger.Frequency.CONFIG_LOAD.value);
                mode = fallback.getMode();
            }

            return switch (mode) {
                case BUKKIT ->
                        new BukkitEntitySnapshotConfig.Factory().getFromConfig(config);
                case PACKETEVENTS ->
                        new PacketEventsEntitySnapshotConfig.Factory().getFromConfig(config);
                default -> {
                    Logger.error(new RuntimeException("Unsupported entity snapshot mode enum value: " + mode + ", falling back on bukkit"), Logger.Frequency.CONFIG_LOAD.value);
                    yield new BukkitEntitySnapshotConfig.Factory().getFromConfig(config);
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