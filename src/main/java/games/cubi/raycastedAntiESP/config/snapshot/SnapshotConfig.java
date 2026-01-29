package games.cubi.raycastedAntiESP.config.snapshot;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.block.BlockSnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.block.BukkitBlockSnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.entity.EntitySnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.tileentity.TileEntitySnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SnapshotConfig(BlockSnapshotConfig blockSnapshotConfig, EntitySnapshotConfig entitySnapshotConfig,
                             TileEntitySnapshotConfig tileEntitySnapshotConfig) implements Config {

    public @Nullable BukkitBlockSnapshotConfig getBukkitBlockSnapshotConfig() {
        if (blockSnapshotConfig instanceof BukkitBlockSnapshotConfig bukkitBlockSnapshotConfig) {
            return bukkitBlockSnapshotConfig;
        }
        return null;
    }

    public static class Factory implements ConfigFactory<SnapshotConfig> {
        private final ConfigFactory<BlockSnapshotConfig> blockSnapshotConfigFactory;
        private final ConfigFactory<EntitySnapshotConfig> entitySnapshotConfigFactory;
        private final ConfigFactory<TileEntitySnapshotConfig> tileEntitySnapshotConfigFactory;

        public final static String PATH = "snapshot";

        public Factory() {
            this.blockSnapshotConfigFactory = new BlockSnapshotConfig.Factory();
            this.entitySnapshotConfigFactory = new EntitySnapshotConfig.Factory();
            this.tileEntitySnapshotConfigFactory = new TileEntitySnapshotConfig.Factory();
        }

        @Override
        public String getFullPath() {
            return PATH;
        }

        @Override
        public @NotNull SnapshotConfig getFromConfig(FileConfiguration config) {
            BlockSnapshotConfig blockSnapshotConfig = blockSnapshotConfigFactory.getFromConfig(config);
            EntitySnapshotConfig entitySnapshotConfig = entitySnapshotConfigFactory.getFromConfig(config);
            TileEntitySnapshotConfig tileEntitySnapshotConfig = tileEntitySnapshotConfigFactory.getFromConfig(config);

            return new SnapshotConfig(blockSnapshotConfig, entitySnapshotConfig, tileEntitySnapshotConfig);
        }

        @Override
        public @NotNull ConfigFactory<SnapshotConfig> setDefaults(FileConfiguration config) {
            blockSnapshotConfigFactory.setDefaults(config);
            entitySnapshotConfigFactory.setDefaults(config);
            tileEntitySnapshotConfigFactory.setDefaults(config);
            return this;
        }
    }
}
