package games.cubi.raycastedantiesp.core.config.raycast;

import org.spongepowered.configurate.ConfigurationNode;

public class PlayerConfig extends RaycastConfig {
    private PlayerConfig(RaycastConfig config) {
        super(config.enabled(), config.hideSoundsWhenHidden(), config.getMaxOccludingCount(), config.getAlwaysShowRadius(),
                config.getRaycastRadius(), config.hideOnSpawnDistance(), config.getVisibleRecheckIntervalTicks());
    }

    public static PlayerConfig load(ConfigurationNode node, String path) {
        return new PlayerConfig(RaycastConfig.load(node, path, true));
    }
}
