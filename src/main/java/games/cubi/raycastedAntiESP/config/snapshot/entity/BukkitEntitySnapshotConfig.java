package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BukkitEntitySnapshotConfig extends EntitySnapshotConfig{
    public BukkitEntitySnapshotConfig() {
        super(EntityMode.BUKKIT);
    }

    public static final BukkitEntitySnapshotConfig DEFAULT =
            new BukkitEntitySnapshotConfig();

    public static class Factory implements ConfigFactory<BukkitEntitySnapshotConfig> {
        public final static String PATH = ".bukkit";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + EntitySnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull BukkitEntitySnapshotConfig getFromConfig(FileConfiguration config) {
            return new BukkitEntitySnapshotConfig();
        }

        @Override
        public @NotNull ConfigFactory<BukkitEntitySnapshotConfig> setDefaults(FileConfiguration config) {
            return this;
        }
    }
}
