package games.cubi.raycastedantiesp.core.config.raycast;

import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigReader;
import org.spongepowered.configurate.ConfigurationNode;

public record ChunkSectionConfig(boolean enabled, int maxOccludingCount, int alwaysShowRadiusChunks, int visibleRecheckIntervalTicks) implements Config {
    public static ChunkSectionConfig load(ConfigurationNode node, String path) {
        return new ChunkSectionConfig(
                ConfigReader.bool(ConfigReader.node(node, "enabled"), path + ".enabled"),
                ConfigReader.integer(ConfigReader.node(node, "max-occluding-count"), path + ".max-occluding-count"),
                ConfigReader.integer(ConfigReader.node(node, "always-show-radius-chunks"), path + ".always-show-radius-chunks"),
                ConfigReader.integer(ConfigReader.node(node, "visible-recheck-interval-ticks"), path + ".visible-recheck-interval-ticks")
        );
    }
}
