package games.cubi.raycastedAntiESP.config.snapshot.block;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BlockSnapshotConfigFactory implements ConfigFactory<BlockSnapshotConfig> {
    private static final String PATH = "snapshot.block";
    @Override
    public @NotNull BlockSnapshotConfig getFromConfig(FileConfiguration config, BlockSnapshotConfig defaults) {
        if (defaults instanceof BukkitBlockSnapshotConfig bukkitDefaults) {
            return getBukkitConfig(config, bukkitDefaults, bukkitDefaults.getPathName());
        }
        Logger.error(new RuntimeException("Unknown BlockSnapshotConfig type: " + defaults.getClass().getName()), 2);
        return defaults;
    }

    private BukkitBlockSnapshotConfig getBukkitConfig(FileConfiguration config, BukkitBlockSnapshotConfig defaults, String bukkitPath) {
        String fullPath = PATH + "." + bukkitPath;
        int snapshotRate = config.getInt(fullPath + ".snapshot-refresh-rate-seconds", defaults.getRefreshRateSeconds());

        if (snapshotRate < 1) {
            Logger.warning("Invalid snapshot refresh rate for " + fullPath + ".snapshotRefreshRateSeconds: " + snapshotRate + ". Using default: " + defaults.getRefreshRateSeconds(), 3);
            snapshotRate = defaults.getRefreshRateSeconds();
        }

        if (defaults.getMode() == BlockSnapshotConfig.Mode.UNSAFE_ASYNC_BUKKIT) {
            return new UnsafeAsyncBukkit(snapshotRate);
        }
        return new SyncBukkit(snapshotRate);
    }

    @Override
    public void setDefaults(FileConfiguration config, BlockSnapshotConfig defaults) {

    }
}
