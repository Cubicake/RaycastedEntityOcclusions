package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketEventsTileEntitySnapshotConfig extends TileEntitySnapshotConfig {
    protected PacketEventsTileEntitySnapshotConfig() {
        super(TileEntitySnapshotMode.PACKETEVENTS);
    }

    public static final PacketEventsTileEntitySnapshotConfig DEFAULT =
            new PacketEventsTileEntitySnapshotConfig();

    public static class Factory implements ConfigFactory<PacketEventsTileEntitySnapshotConfig> {
        public static final String PATH = ".packetevents";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + TileEntitySnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull PacketEventsTileEntitySnapshotConfig getFromConfig(FileConfiguration config) {
            return new PacketEventsTileEntitySnapshotConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsTileEntitySnapshotConfig> setDefaults(FileConfiguration config) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
