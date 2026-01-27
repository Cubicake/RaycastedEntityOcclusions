package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketEventsTileEntitySnapshotConfig extends TileEntitySnapshotConfig {
    protected PacketEventsTileEntitySnapshotConfig(TileEntitySnapshotMode mode) {
        super(mode);
    }

    public static final PacketEventsTileEntitySnapshotConfig DEFAULT =
            new PacketEventsTileEntitySnapshotConfig(
                    TileEntitySnapshotMode.PACKETEVENTS
            );

    public static class Factory implements ConfigFactory<PacketEventsTileEntitySnapshotConfig> {
        public static final String PATH = ".packetevents";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + TileEntitySnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull PacketEventsTileEntitySnapshotConfig getFromConfig(FileConfiguration config, PacketEventsTileEntitySnapshotConfig defaults) {
            if (defaults != null) {
                return new PacketEventsTileEntitySnapshotConfig(defaults.getMode());
            }
            return new PacketEventsTileEntitySnapshotConfig(TileEntitySnapshotMode.PACKETEVENTS);
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsTileEntitySnapshotConfig> setDefaults(FileConfiguration config, @Nullable PacketEventsTileEntitySnapshotConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
