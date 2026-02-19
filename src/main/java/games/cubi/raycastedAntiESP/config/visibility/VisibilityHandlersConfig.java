package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.block.BlockVisibilityHandlerConfig;
import games.cubi.raycastedAntiESP.config.visibility.entity.EntityVisibilityHandlerConfig;
import games.cubi.raycastedAntiESP.config.visibility.tileentity.TileEntityVisibilityHandlerConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public record VisibilityHandlersConfig(BlockVisibilityHandlerConfig blockConfig,
                                       EntityVisibilityHandlerConfig entityConfig,
                                       TileEntityVisibilityHandlerConfig tileEntityConfig) implements Config {

    public static class Factory implements ConfigFactory<VisibilityHandlersConfig> {
        public static final String PATH = "visibility-handlers";

        private final BlockVisibilityHandlerConfig.Factory blockFactory = new BlockVisibilityHandlerConfig.Factory();
        private final EntityVisibilityHandlerConfig.Factory entityFactory = new EntityVisibilityHandlerConfig.Factory();
        private final TileEntityVisibilityHandlerConfig.Factory tileEntityFactory = new TileEntityVisibilityHandlerConfig.Factory();

        @Override
        public String getFullPath() {
            return PATH;
        }

        @Override
        public @NotNull VisibilityHandlersConfig getFromConfig(ConfigurationNode config) {
            BlockVisibilityHandlerConfig blockConfig = blockFactory.getFromConfig(config);
            EntityVisibilityHandlerConfig entityConfig = entityFactory.getFromConfig(config);
            TileEntityVisibilityHandlerConfig tileEntityConfig = tileEntityFactory.getFromConfig(config);
            return new VisibilityHandlersConfig(blockConfig, entityConfig, tileEntityConfig);
        }

        @Override
        public @NotNull ConfigFactory<VisibilityHandlersConfig> setDefaults(ConfigurationNode config) {
            blockFactory.setDefaults(config);
            entityFactory.setDefaults(config);
            tileEntityFactory.setDefaults(config);
            return this;
        }
    }
}
