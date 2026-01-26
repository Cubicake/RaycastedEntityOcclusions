package games.cubi.raycastedAntiESP.config;

import games.cubi.raycastedAntiESP.config.snapshot.SnapshotConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public interface ConfigFactory<T extends Config> {
    @NotNull T getFromConfig(FileConfiguration config, T defaults);
    void setDefaults(FileConfiguration config, T defaults);
}
