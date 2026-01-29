package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitTileEntitySnapshotConfig extends TileEntitySnapshotConfig {
    protected BukkitTileEntitySnapshotConfig() {
        super(TileEntitySnapshotMode.BUKKIT);
    }

    public static final BukkitTileEntitySnapshotConfig DEFAULT =
            new BukkitTileEntitySnapshotConfig();

    public static class Factory implements ConfigFactory<BukkitTileEntitySnapshotConfig> {
        public static final String PATH = ".bukkit";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + TileEntitySnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull BukkitTileEntitySnapshotConfig getFromConfig(FileConfiguration config) {
            return new BukkitTileEntitySnapshotConfig();
        }

        @Override
        public @NotNull ConfigFactory<BukkitTileEntitySnapshotConfig> setDefaults(FileConfiguration config) {
            return this;
        }
    }
}
