package games.cubi.raycastedAntiESP.config.snapshot;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.Defaults;
import games.cubi.raycastedAntiESP.config.snapshot.block.BlockSnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.block.BukkitBlockSnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.entity.EntitySnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.tileentity.TileEntitySnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SnapshotConfig implements Config {

    private final BlockSnapshotConfig blockSnapshotConfig;
    private final EntitySnapshotConfig entitySnapshotConfig;
    private final TileEntitySnapshotConfig tileEntitySnapshotConfig;

    public SnapshotConfig(BlockSnapshotConfig blockSnapshotConfig, EntitySnapshotConfig entitySnapshotConfig, TileEntitySnapshotConfig tileEntitySnapshotConfig) {
        this.blockSnapshotConfig = blockSnapshotConfig;
        this.entitySnapshotConfig = entitySnapshotConfig;
        this.tileEntitySnapshotConfig = tileEntitySnapshotConfig;
    }

    public BlockSnapshotConfig getBlockSnapshotConfig() {
        return blockSnapshotConfig;
    }
    public EntitySnapshotConfig getEntitySnapshotConfig() {
        return entitySnapshotConfig;
    }
    public TileEntitySnapshotConfig getTileEntitySnapshotConfig() {
        return tileEntitySnapshotConfig;
    }

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
        public @NotNull SnapshotConfig getFromConfig(FileConfiguration config, @Nullable SnapshotConfig defaults) {
            BlockSnapshotConfig blockSnapshotConfig = blockSnapshotConfigFactory.getFromConfig(config, BlockSnapshotConfig.DEFAULT);
            //EntitySnapshotConfig entitySnapshotConfig = entitySnapshotConfigFactory.getFromConfig(config, defaults.getEntitySnapshotConfig());
            //TileEntitySnapshotConfig tileEntitySnapshotConfig = tileEntitySnapshotConfigFactory.getFromConfig(config, defaults.getTileEntitySnapshotConfig());

            return new SnapshotConfig(blockSnapshotConfig, null, null);
        }

        @Override
        public @NotNull ConfigFactory<SnapshotConfig> setDefaults(FileConfiguration config, SnapshotConfig defaults) {
            blockSnapshotConfigFactory.setDefaults(config, BlockSnapshotConfig.DEFAULT);
            //entitySnapshotConfigFactory.setDefaults(config, defaults.getEntitySnapshotConfig());
            //tileEntitySnapshotConfigFactory.setDefaults(config, defaults.getTileEntitySnapshotConfig());
            return this;
        }
    }
}
