package games.cubi.raycastedAntiESP.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigFactory<T extends Config> {
    String getFullPath();
    @NotNull T getFromConfig(FileConfiguration config);

    /**
     * @param config
     * @param defaults The default config object provided will only contain correct values for the current level, not nested levels. If the current level has no config values, defaults will be null.
     * @return The ConfigFactory instance for getFromConfig, which may be the same instance or an implementation-specific instance from the same level. The return object is to allow for calling getFromConfig on the correct factory.
     */
    @NotNull ConfigFactory<T> setDefaults(FileConfiguration config);
}
