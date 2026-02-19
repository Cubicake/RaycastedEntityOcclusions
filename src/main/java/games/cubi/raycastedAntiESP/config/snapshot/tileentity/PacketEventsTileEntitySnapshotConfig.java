package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.spongepowered.configurate.ConfigurationNode;
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
        public @NotNull PacketEventsTileEntitySnapshotConfig getFromConfig(ConfigurationNode config) {
            return new PacketEventsTileEntitySnapshotConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsTileEntitySnapshotConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
