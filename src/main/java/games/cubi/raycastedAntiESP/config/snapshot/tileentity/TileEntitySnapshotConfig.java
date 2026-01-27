package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.entity.EntitySnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public interface TileEntitySnapshotConfig extends Config {
    public static class Factory implements ConfigFactory<TileEntitySnapshotConfig> {
        @Override
        public String getFullPath() {
            return "";
        }

        @Override
        public @NotNull TileEntitySnapshotConfig getFromConfig(FileConfiguration config, TileEntitySnapshotConfig defaults) {
            return null;
        }

        @Override
        public @NotNull ConfigFactory<TileEntitySnapshotConfig> setDefaults(FileConfiguration config, TileEntitySnapshotConfig defaults) {
            return null;
        }
    }
}
