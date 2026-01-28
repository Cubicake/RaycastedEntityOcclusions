package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketEventsEntitySnapshotConfig extends EntitySnapshotConfig {
    protected PacketEventsEntitySnapshotConfig(EntityMode mode) {
        super(mode);
    }

    public static final PacketEventsEntitySnapshotConfig DEFAULT =
            new PacketEventsEntitySnapshotConfig(
                    EntityMode.PACKETEVENTS
            );

    public static class Factory implements ConfigFactory<PacketEventsEntitySnapshotConfig> {
        public static final String PATH = ".packetevents";

        @Override
        public String getFullPath() {
            return SnapshotConfig.Factory.PATH + EntitySnapshotConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull PacketEventsEntitySnapshotConfig getFromConfig(FileConfiguration config, PacketEventsEntitySnapshotConfig defaults) {
            if (defaults != null) {
                return new PacketEventsEntitySnapshotConfig(defaults.getMode());
            }
            return new PacketEventsEntitySnapshotConfig(EntityMode.PACKETEVENTS);
        }

        @Override
        public @NotNull ConfigFactory<PacketEventsEntitySnapshotConfig> setDefaults(FileConfiguration config, @Nullable PacketEventsEntitySnapshotConfig defaults) {
            config.addDefault(getFullPath(), null);
            return this;
        }
    }
}
