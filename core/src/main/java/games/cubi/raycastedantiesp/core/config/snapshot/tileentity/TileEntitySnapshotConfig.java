package games.cubi.raycastedantiesp.core.config.snapshot.tileentity;

import games.cubi.logs.Frequency;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigFactory;
import games.cubi.raycastedantiesp.core.config.snapshot.SnapshotConfig;
import org.spongepowered.configurate.ConfigurationNode;
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
                    TileEntitySnapshotMode.PACKETEVENTS
            );

    public static class Factory implements ConfigFactory<TileEntitySnapshotConfig> {
        public static final String PATH = ".tile-entity";
        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull TileEntitySnapshotConfig getFromConfig(ConfigurationNode config) {
            TileEntitySnapshotConfig fallback = DEFAULT;
            String modeName = ConfigNodeUtil.getString(config, getFullPath()+".mode", fallback.getName());
            TileEntitySnapshotMode mode = TileEntitySnapshotMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid tile entity snapshot mode in config, defaulting to " + fallback.getName(), Frequency.CONFIG_LOAD.value, TileEntitySnapshotConfig.class);
                mode = fallback.mode;
            }
            if (mode != TileEntitySnapshotMode.PACKETEVENTS) {
                Logger.warning("Tile entity snapshot mode '" + mode.getName() + "' is no longer supported, coercing to packetevents.", Frequency.CONFIG_LOAD.value, TileEntitySnapshotConfig.class);
                mode = TileEntitySnapshotMode.PACKETEVENTS;
            }

            return switch (mode) {
                case PACKETEVENTS ->
                        new PacketEventsTileEntitySnapshotConfig.Factory().getFromConfig(config);
                default -> {
                    Logger.error(new RuntimeException("Unsupported tile entity snapshot mode enum value: " + mode + ", falling back on packetevents"), Frequency.CONFIG_LOAD.value, TileEntitySnapshotConfig.class);
                    yield new PacketEventsTileEntitySnapshotConfig.Factory().getFromConfig(config);
                }
            };
        }

        @Override
        public @NotNull ConfigFactory<TileEntitySnapshotConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath()+".mode", DEFAULT.getName());
            new BukkitTileEntitySnapshotConfig.Factory().setDefaults(config);
            new PacketEventsTileEntitySnapshotConfig.Factory().setDefaults(config);
            return this;
        }
    }
}
