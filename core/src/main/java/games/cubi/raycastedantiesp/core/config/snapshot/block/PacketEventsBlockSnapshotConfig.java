package games.cubi.raycastedantiesp.core.config.snapshot.block;

import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.ConfigFactory;
import games.cubi.raycastedantiesp.core.config.snapshot.SnapshotConfig;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

public class PacketEventsBlockSnapshotConfig extends BlockSnapshotConfig {
    protected PacketEventsBlockSnapshotConfig() {
        super(BlockMode.PACKETEVENTS);
    }

    public static final PacketEventsBlockSnapshotConfig DEFAULT = new PacketEventsBlockSnapshotConfig();

    public static class Factory implements ConfigFactory<PacketEventsBlockSnapshotConfig> {
        public static final String PATH = ".packetevents";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + BlockSnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull PacketEventsBlockSnapshotConfig getFromConfig(ConfigurationNode config) {
            return new PacketEventsBlockSnapshotConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsBlockSnapshotConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
