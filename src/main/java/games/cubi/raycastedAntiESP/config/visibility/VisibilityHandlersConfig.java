package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VisibilityHandlersConfig implements Config {
    private final BlockVisibilityHandlerConfig blockConfig;
    private final EntityVisibilityHandlerConfig entityConfig;
    private final TileEntityVisibilityHandlerConfig tileEntityConfig;

    public VisibilityHandlersConfig(BlockVisibilityHandlerConfig blockConfig,
                                    EntityVisibilityHandlerConfig entityConfig,
                                    TileEntityVisibilityHandlerConfig tileEntityConfig) {
        this.blockConfig = blockConfig;
        this.entityConfig = entityConfig;
        this.tileEntityConfig = tileEntityConfig;
    }

    public BlockVisibilityHandlerConfig getBlockConfig() {
        return blockConfig;
    }

    public EntityVisibilityHandlerConfig getEntityConfig() {
        return entityConfig;
    }

    public TileEntityVisibilityHandlerConfig getTileEntityConfig() {
        return tileEntityConfig;
    }

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
        public @NotNull VisibilityHandlersConfig getFromConfig(FileConfiguration config,
                                                               @Nullable VisibilityHandlersConfig defaults) {
            BlockVisibilityHandlerConfig blockDefaults =
                defaults != null ? defaults.getBlockConfig() : BlockVisibilityHandlerConfig.DEFAULT;
            EntityVisibilityHandlerConfig entityDefaults =
                defaults != null ? defaults.getEntityConfig() : EntityVisibilityHandlerConfig.DEFAULT;
            TileEntityVisibilityHandlerConfig tileEntityDefaults =
                defaults != null ? defaults.getTileEntityConfig() : TileEntityVisibilityHandlerConfig.DEFAULT;
            BlockVisibilityHandlerConfig blockConfig =
                blockFactory.getFromConfig(config, blockDefaults);
            EntityVisibilityHandlerConfig entityConfig =
                entityFactory.getFromConfig(config, entityDefaults);
            TileEntityVisibilityHandlerConfig tileEntityConfig =
                tileEntityFactory.getFromConfig(config, tileEntityDefaults);
            return new VisibilityHandlersConfig(blockConfig, entityConfig, tileEntityConfig);
        }

        @Override
        public @NotNull ConfigFactory<VisibilityHandlersConfig> setDefaults(FileConfiguration config,
                                                                            @Nullable VisibilityHandlersConfig defaults) {
            BlockVisibilityHandlerConfig blockDefaults =
                defaults != null ? defaults.getBlockConfig() : BlockVisibilityHandlerConfig.DEFAULT;
            EntityVisibilityHandlerConfig entityDefaults =
                defaults != null ? defaults.getEntityConfig() : EntityVisibilityHandlerConfig.DEFAULT;
            TileEntityVisibilityHandlerConfig tileEntityDefaults =
                defaults != null ? defaults.getTileEntityConfig() : TileEntityVisibilityHandlerConfig.DEFAULT;
            blockFactory.setDefaults(config, blockDefaults);
            entityFactory.setDefaults(config, entityDefaults);
            tileEntityFactory.setDefaults(config, tileEntityDefaults);
            return this;
        }
    }
}
