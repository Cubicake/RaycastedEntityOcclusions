package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntitySnapshotConfig implements Config {
    private final TileEntitySnapshotMode mode;

    public TileEntitySnapshotConfig(TileEntitySnapshotMode mode) {
        this.mode = mode;
    }

    public TileEntitySnapshotMode getMode() {
        return mode;
    }

    public String getName() {
        return mode.getName();
    }

    public String getPathName() {
        return mode.getPathName();
    }

    public static final TileEntitySnapshotConfig DEFAULT =
            new TileEntitySnapshotConfig(
                    TileEntitySnapshotMode.BUKKIT
            );

    public static class Factory implements ConfigFactory<TileEntitySnapshotConfig> {
        public static final String PATH = ".tile-entity";
        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull TileEntitySnapshotConfig getFromConfig(FileConfiguration config, TileEntitySnapshotConfig defaults) {
            TileEntitySnapshotConfig fallback = defaults != null ? defaults : DEFAULT;
            String modeName = config.getString(getFullPath()+".mode", fallback.getName());
            TileEntitySnapshotMode mode = TileEntitySnapshotMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid tile entity snapshot mode in config, defaulting to " + fallback.getName(), 3);
                mode = fallback.mode;
            }

            return switch (mode) {
                case BUKKIT -> new BukkitTileEntitySnapshotConfig.Factory().getFromConfig(config, BukkitTileEntitySnapshotConfig.DEFAULT);
                case PACKETEVENTS ->
                        throw new UnsupportedOperationException("PacketEvents tile entity snapshot mode is not yet implemented.");
                default -> {
                    Logger.error(new RuntimeException("Unsupported tile entity snapshot mode enum value: " + mode + ", falling back on bukkit"), 3);
                    yield new BukkitTileEntitySnapshotConfig.Factory().getFromConfig(config, BukkitTileEntitySnapshotConfig.DEFAULT);
                }
            };
        }

        @Override
        public @NotNull ConfigFactory<TileEntitySnapshotConfig> setDefaults(FileConfiguration config, @Nullable TileEntitySnapshotConfig defaults) {
            TileEntitySnapshotConfig fallback = defaults != null ? defaults : DEFAULT;
            config.addDefault(getFullPath()+".mode", fallback.getName());
            new BukkitTileEntitySnapshotConfig.Factory().setDefaults(config, null);
            return this;
        }
    }
}
