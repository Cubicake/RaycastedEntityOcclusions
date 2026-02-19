package games.cubi.raycastedAntiESP.config.snapshot.block;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.spongepowered.configurate.ConfigurationNode;
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
        public @NotNull BlockSnapshotConfig getFromConfig(ConfigurationNode config) {
            BlockMode mode = getModeFromConfig(config);
            if (mode == null) {
                Logger.warning("Invalid block snapshot mode in config, defaulting to " + DEFAULT.getMode().getName(), Logger.Frequency.CONFIG_LOAD.value);
                mode = DEFAULT.getMode();
            }

            return switch (mode) {
                case SYNC_BUKKIT, UNSAFE_ASYNC_BUKKIT ->
                        new BukkitBlockSnapshotConfig.Factory(mode).getFromConfig(config);
                case PACKETEVENTS ->
                        throw new UnsupportedOperationException("PacketEvents block snapshot mode is not yet implemented.");
                default -> {
                    Logger.error(new RuntimeException("Unsupported block snapshot mode enum value: " + mode + ", falling back on sync-bukkit"), Logger.Frequency.CONFIG_LOAD.value);
                    yield new BukkitBlockSnapshotConfig.Factory(BlockMode.SYNC_BUKKIT).getFromConfig(config);
                }
            };
        }

        private @Nullable BlockMode getModeFromConfig(ConfigurationNode config) {
            String modeName = ConfigNodeUtil.getString(config, getFullPath()+".mode", BlockSnapshotConfig.DEFAULT.getName());
            return BlockMode.fromString(modeName);
        }

        @Override
        public @NotNull ConfigFactory<BlockSnapshotConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath()+".mode", DEFAULT.getMode().getName());
            new BukkitBlockSnapshotConfig.Factory(null).setDefaults(config);
            return this;
        }
    }

}
