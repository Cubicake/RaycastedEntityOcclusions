package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.block.BlockSnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public interface EntitySnapshotConfig extends Config {

    public static class Factory implements ConfigFactory<EntitySnapshotConfig> {
        @Override
        public String getFullPath() {
            return "";
        }

        @Override
        public @NotNull EntitySnapshotConfig getFromConfig(FileConfiguration config, EntitySnapshotConfig defaults) {
            return null;
        }

        @Override
        public @NotNull ConfigFactory<EntitySnapshotConfig> setDefaults(FileConfiguration config, EntitySnapshotConfig defaults) {
            return null;
        }
    }
}
