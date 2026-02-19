package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketEventsEntitySnapshotConfig extends EntitySnapshotConfig {
    protected PacketEventsEntitySnapshotConfig() {
        super(EntityMode.PACKETEVENTS);
    }

    public static final PacketEventsEntitySnapshotConfig DEFAULT =
            new PacketEventsEntitySnapshotConfig();

    public static class Factory implements ConfigFactory<PacketEventsEntitySnapshotConfig> {
        public static final String PATH = ".packetevents";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + EntitySnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull PacketEventsEntitySnapshotConfig getFromConfig(ConfigurationNode config) {
            return new PacketEventsEntitySnapshotConfig();
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsEntitySnapshotConfig> setDefaults(ConfigurationNode config) {
            ConfigNodeUtil.addDefault(config, getFullPath(), null);
            return this;
        }
    }
}
