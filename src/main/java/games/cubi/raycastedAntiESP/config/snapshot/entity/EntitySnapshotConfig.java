package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.block.BukkitBlockSnapshotConfig;
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

    public static class Factory implements ConfigFactory<EntitySnapshotConfig> {
        public final static String PATH = ".entity";
        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull EntitySnapshotConfig getFromConfig(FileConfiguration config, EntitySnapshotConfig defaults) {
            return null;
        }

        @Override
        public @NotNull ConfigFactory<EntitySnapshotConfig> setDefaults(FileConfiguration config, EntitySnapshotConfig defaults) {
            config.addDefault(getFullPath()+".mode", defaults.getMode().getName());
            new BukkitEntitySnapshotConfig.Factory().setDefaults(config, null);
            return this;
        }
    }
}
