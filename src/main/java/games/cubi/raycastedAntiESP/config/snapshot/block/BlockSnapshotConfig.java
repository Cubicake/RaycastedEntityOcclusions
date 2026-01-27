package games.cubi.raycastedAntiESP.config.snapshot.block;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSnapshotConfig implements Config {
    private final BlockMode mode;

    protected BlockSnapshotConfig(BlockMode mode) {
        this.mode = mode;
    }

    public BlockMode getMode() {
        return mode;
    }

    public String getName() {
        return getMode().getName();
    }

    public String getPathName() {
        return getMode().getPathName();
    }

    public static final BlockSnapshotConfig DEFAULT =
            new BlockSnapshotConfig(
                    BlockMode.SYNC_BUKKIT
            );

    public static class Factory implements ConfigFactory<BlockSnapshotConfig> {
        public static final String PATH = ".block";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull BlockSnapshotConfig getFromConfig(FileConfiguration config, BlockSnapshotConfig defaults) {
            BlockMode mode = getModeFromConfig(config);
            if (mode == null) {
                Logger.warning("Invalid block snapshot mode in config, defaulting to " + defaults.getMode().getName(), 3);
                mode = defaults.getMode();
            }

            return switch (mode) {
                case SYNC_BUKKIT, UNSAFE_ASYNC_BUKKIT ->
                        new BukkitBlockSnapshotConfig.Factory(mode).getFromConfig(config, BukkitBlockSnapshotConfig.DEFAULT);
                case PACKETEVENTS ->
                        throw new UnsupportedOperationException("PacketEvents block snapshot mode is not yet implemented.");
                default -> {
                    Logger.error(new RuntimeException("Unsupported block snapshot mode enum value: " + mode + ", falling back on sync-bukkit"), 3);
                    yield new BukkitBlockSnapshotConfig.Factory(BlockMode.SYNC_BUKKIT).getFromConfig(config, BukkitBlockSnapshotConfig.DEFAULT);
                }
            };
        }

        private @Nullable BlockMode getModeFromConfig(FileConfiguration config) {
            String modeName = config.getString(getFullPath()+".mode", BlockSnapshotConfig.DEFAULT.getName());
            return BlockMode.fromString(modeName);
        }

        @Override
        public @NotNull ConfigFactory<BlockSnapshotConfig> setDefaults(FileConfiguration config, BlockSnapshotConfig defaults) {
            // Save the mode
            config.addDefault(getFullPath()+".mode", defaults.getMode().getName());
            new BukkitBlockSnapshotConfig.Factory(null).setDefaults(config, BukkitBlockSnapshotConfig.DEFAULT);
            return this;
        }
    }

}
