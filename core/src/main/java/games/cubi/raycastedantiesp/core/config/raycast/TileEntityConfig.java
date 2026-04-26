package games.cubi.raycastedantiesp.core.config.raycast;

import org.spongepowered.configurate.ConfigurationNode;

public class TileEntityConfig extends RaycastConfig {
    private TileEntityConfig(RaycastConfig config) {
        super(config.enabled(), false, config.getMaxOccludingCount(), config.getAlwaysShowRadius(),
                config.getRaycastRadius(), config.hideOnSpawnDistance(), config.getVisibleRecheckIntervalTicks());
    }

    public static TileEntityConfig load(ConfigurationNode node, String path) {
        return new TileEntityConfig(RaycastConfig.load(node, path, false));
    }
}
