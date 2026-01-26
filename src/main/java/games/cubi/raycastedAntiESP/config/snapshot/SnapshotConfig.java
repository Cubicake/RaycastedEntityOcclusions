package games.cubi.raycastedAntiESP.config.snapshot;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.snapshot.block.BlockSnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.block.BukkitBlockSnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.entity.EntitySnapshotConfig;
import games.cubi.raycastedAntiESP.config.snapshot.tileentity.TileEntitySnapshotConfig;
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
}
