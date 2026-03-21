package games.cubi.raycastedantiesp.paper.config.snapshot.tileentity;

import games.cubi.raycastedantiesp.paper.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.paper.config.ConfigFactory;
import games.cubi.raycastedantiesp.paper.config.snapshot.SnapshotConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

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
