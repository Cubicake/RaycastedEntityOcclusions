package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.block.BlockSnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitEntitySnapshotConfig extends EntitySnapshotConfig{
    public BukkitEntitySnapshotConfig(EntityMode mode) {
        super(mode);
    }

    public static final BukkitEntitySnapshotConfig DEFAULT =
            new BukkitEntitySnapshotConfig(
                    EntityMode.BUKKIT
            );

    public static class Factory implements ConfigFactory<BukkitEntitySnapshotConfig> {
        public final static String PATH = ".bukkit";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + EntitySnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull BukkitEntitySnapshotConfig getFromConfig(FileConfiguration config, BukkitEntitySnapshotConfig defaults) {
            return new BukkitEntitySnapshotConfig(EntityMode.BUKKIT);
        }

        @Override
        public @NotNull ConfigFactory<BukkitEntitySnapshotConfig> setDefaults(FileConfiguration config, @Nullable BukkitEntitySnapshotConfig defaults) {
            return this;
        }
    }
}
