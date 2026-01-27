package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
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
        public @NotNull EntitySnapshotConfig getFromConfig(FileConfiguration config, EntitySnapshotConfig defaults) {
            EntitySnapshotConfig fallback = defaults != null ? defaults : DEFAULT;
            String modeName = config.getString(getFullPath()+".mode", fallback.getMode().getName());
            EntityMode mode = EntityMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid entity snapshot mode in config, defaulting to " + fallback.getMode().getName(), 3);
                mode = fallback.getMode();
            }

            return switch (mode) {
                case BUKKIT ->
                        new BukkitEntitySnapshotConfig.Factory().getFromConfig(config, BukkitEntitySnapshotConfig.DEFAULT);
                case PACKETEVENTS ->
                        throw new UnsupportedOperationException("PacketEvents entity snapshot mode is not yet implemented.");
                default -> {
                    Logger.error(new RuntimeException("Unsupported entity snapshot mode enum value: " + mode + ", falling back on bukkit"), 3);
                    yield new BukkitEntitySnapshotConfig.Factory().getFromConfig(config, BukkitEntitySnapshotConfig.DEFAULT);
                }
            };
        }

        @Override
        public @NotNull ConfigFactory<EntitySnapshotConfig> setDefaults(FileConfiguration config, EntitySnapshotConfig defaults) {
            EntitySnapshotConfig fallback = defaults != null ? defaults : DEFAULT;
            config.addDefault(getFullPath()+".mode", fallback.getMode().getName());
            new BukkitEntitySnapshotConfig.Factory().setDefaults(config, null);
            return this;
        }
    }
}
