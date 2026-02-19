package games.cubi.raycastedAntiESP.config.snapshot.block;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BukkitBlockSnapshotConfig extends BlockSnapshotConfig {
    private final int refreshRateSeconds;

    protected BukkitBlockSnapshotConfig(int refreshRateSeconds, BlockMode mode) {
        super(mode);
        this.refreshRateSeconds = refreshRateSeconds;
    }

    public int getRefreshRateSeconds() {
        return refreshRateSeconds;
    }

    public static final BukkitBlockSnapshotConfig DEFAULT =
            new BukkitBlockSnapshotConfig(
                    60,
                    null
            ); // Mode is null as this is used for both modes. The default mode is set on BlockSnapshotConfig

    static class Factory implements ConfigFactory<BukkitBlockSnapshotConfig> {
        private final BlockMode mode;

        public Factory(BlockMode mode) {
            this.mode = mode;
        }

        @Override
        public String getFullPath() {
            return getPathUpToBlock() + mode.getPathName();
        }

        private String getPathUpToBlock() {
            return SnapshotConfig.Factory.PATH + BlockSnapshotConfig.Factory.PATH;
        }

        @Override
        public @NotNull BukkitBlockSnapshotConfig getFromConfig(FileConfiguration config) {
            String fullPath = getFullPath();
            int snapshotRate = config.getInt(fullPath + ".refresh-interval", DEFAULT.getRefreshRateSeconds());

            if (snapshotRate < 1) {
                snapshotRate = DEFAULT.getRefreshRateSeconds();
            }

            return new BukkitBlockSnapshotConfig(snapshotRate, mode);
        }

        @Override
        public @NotNull ConfigFactory<BukkitBlockSnapshotConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getPathUpToBlock() + BlockMode.SYNC_BUKKIT.getPathName() + ".refresh-interval", DEFAULT.getRefreshRateSeconds());
            config.addDefault(getPathUpToBlock() + BlockMode.UNSAFE_ASYNC_BUKKIT.getPathName() + ".refresh-interval", DEFAULT.getRefreshRateSeconds());
            return this;
        }
    }
}
