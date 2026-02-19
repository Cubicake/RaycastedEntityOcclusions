package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

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
        public @NotNull TileEntitySnapshotConfig getFromConfig(FileConfiguration config) {
            TileEntitySnapshotConfig fallback = DEFAULT;
            String modeName = config.getString(getFullPath()+".mode", fallback.getName());
            TileEntitySnapshotMode mode = TileEntitySnapshotMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid tile entity snapshot mode in config, defaulting to " + fallback.getName(), Logger.Frequency.CONFIG_LOAD.value);
                mode = fallback.mode;
            }

            return switch (mode) {
                case BUKKIT -> new BukkitTileEntitySnapshotConfig.Factory().getFromConfig(config);
                case PACKETEVENTS ->
                        new PacketEventsTileEntitySnapshotConfig.Factory().getFromConfig(config);
                default -> {
                    Logger.error(new RuntimeException("Unsupported tile entity snapshot mode enum value: " + mode + ", falling back on bukkit"), Logger.Frequency.CONFIG_LOAD.value);
                    yield new BukkitTileEntitySnapshotConfig.Factory().getFromConfig(config);
                }
            };
        }

        @Override
        public @NotNull ConfigFactory<TileEntitySnapshotConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getFullPath()+".mode", DEFAULT.getName());
            new BukkitTileEntitySnapshotConfig.Factory().setDefaults(config);
            new PacketEventsTileEntitySnapshotConfig.Factory().setDefaults(config);
            return this;
        }
    }
}
