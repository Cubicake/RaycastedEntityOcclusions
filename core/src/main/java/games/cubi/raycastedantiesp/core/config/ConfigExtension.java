package games.cubi.raycastedantiesp.core.config;

import org.spongepowered.configurate.ConfigurationNode;

public interface ConfigExtension<T extends Config> {
    Class<T> type();

    T load(ConfigurationNode config, BlockProcessorConfig blockProcessorConfig);

    default boolean requiresRestart(T startupConfig, T nextConfig) {
        return !startupConfig.equals(nextConfig);
    }
}
