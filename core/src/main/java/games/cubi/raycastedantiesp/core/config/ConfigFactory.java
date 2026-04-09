package games.cubi.raycastedantiesp.core.config;

import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public interface ConfigFactory<T extends Config> {
    String getFullPath();
    @NotNull T getFromConfig(ConfigurationNode config);

    /**
     * @param config
     * @return The ConfigFactory instance for getFromConfig, which may be the same instance or an implementation-specific instance from the same level. The return object is to allow for calling getFromConfig on the correct factory.
     */
    @NotNull ConfigFactory<T> setDefaults(ConfigurationNode config);
}
