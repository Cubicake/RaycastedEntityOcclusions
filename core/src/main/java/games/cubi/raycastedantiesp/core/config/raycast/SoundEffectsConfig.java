package games.cubi.raycastedantiesp.core.config.raycast;

import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigReader;
import org.spongepowered.configurate.ConfigurationNode;

public record SoundEffectsConfig(
        boolean enabled,
        int maxOccludingCount,
        int alwaysPlayRadius,
        int raycastRadius
) implements Config {
    public static SoundEffectsConfig load(ConfigurationNode node, String path) {
        return new SoundEffectsConfig(
                ConfigReader.bool(ConfigReader.node(node, "enabled"), path + ".enabled"),
                ConfigReader.integer(ConfigReader.node(node, "max-occluding-count"), path + ".max-occluding-count"),
                ConfigReader.integer(ConfigReader.node(node, "always-play-radius"), path + ".always-play-radius"),
                ConfigReader.integer(ConfigReader.node(node, "raycast-radius"), path + ".raycast-radius")
        );
    }
}
