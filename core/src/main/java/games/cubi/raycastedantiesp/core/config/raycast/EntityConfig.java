package games.cubi.raycastedantiesp.core.config.raycast;

import org.spongepowered.configurate.ConfigurationNode;

public class EntityConfig extends RaycastConfig {
    private EntityConfig(RaycastConfig config) {
        super(config.enabled(), config.hideSoundsWhenHidden(), config.getMaxOccludingCount(), config.getAlwaysShowRadius(),
                config.getRaycastRadius(), config.hideOnSpawnDistance(), config.getVisibleRecheckIntervalTicks());
    }

    public static EntityConfig load(ConfigurationNode node, String path) {
        return new EntityConfig(RaycastConfig.load(node, path, true));
    }
}
