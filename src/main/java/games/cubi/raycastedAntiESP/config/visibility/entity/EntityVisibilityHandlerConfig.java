package games.cubi.raycastedAntiESP.config.visibility.entity;

import games.cubi.raycastedAntiESP.config.ConfigNodeUtil;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import games.cubi.raycastedAntiESP.config.visibility.VisibilityHandlersConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityVisibilityHandlerConfig implements Config {
    private final EntityVisibilityHandlerMode mode;

    protected EntityVisibilityHandlerConfig(EntityVisibilityHandlerMode mode) {
        this.mode = mode;
    }

    public EntityVisibilityHandlerMode getMode() {
        return mode;
    }

    public String getName() {
        return mode.getName();
    }

    public String getPathName() {
        return mode.getPathName();
    }

    public static final EntityVisibilityHandlerConfig DEFAULT =
        new EntityVisibilityHandlerConfig(EntityVisibilityHandlerMode.BUKKIT);

    public static class Factory implements ConfigFactory<EntityVisibilityHandlerConfig> {
        public static final String PATH = ".entity";
        private final BukkitEntityVisibilityHandlerConfig.Factory bukkitFactory =
            new BukkitEntityVisibilityHandlerConfig.Factory(EntityVisibilityHandlerMode.BUKKIT);
        private final PacketEventsEntityVisibilityHandlerConfig.Factory packetEventsFactory =
            new PacketEventsEntityVisibilityHandlerConfig.Factory(EntityVisibilityHandlerMode.PACKETEVENTS);

        @Override
        public String getFullPath() {
            return VisibilityHandlersConfig.Factory.PATH + PATH;
        }

        @Override
        public @NotNull EntityVisibilityHandlerConfig getFromConfig(ConfigurationNode config) {
            EntityVisibilityHandlerMode mode = readMode(config);
            return switch (mode) {
                case BUKKIT -> bukkitFactory.getFromConfig(config);
                case PACKETEVENTS -> packetEventsFactory.getFromConfig(
                    config);
            };
        }

        private EntityVisibilityHandlerMode readMode(ConfigurationNode config) {
            String modeName = ConfigNodeUtil.getString(config, getFullPath() + ".mode", EntityVisibilityHandlerConfig.DEFAULT.getName());
            EntityVisibilityHandlerMode mode = EntityVisibilityHandlerMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid entity visibility handler mode in config, defaulting to " + EntityVisibilityHandlerConfig.DEFAULT.getName(), 3);
                mode = EntityVisibilityHandlerConfig.DEFAULT.getMode();
            }
            return mode;
        }

        @Override
        public @NotNull ConfigFactory<EntityVisibilityHandlerConfig> setDefaults(ConfigurationNode config) {
            EntityVisibilityHandlerConfig fallback = DEFAULT;
            ConfigNodeUtil.addDefault(config, getFullPath() + ".mode", fallback.getName());
            EntityVisibilityHandlerMode mode = fallback.getMode();
            switch (mode) {
                case BUKKIT -> bukkitFactory.setDefaults(config);
                case PACKETEVENTS -> packetEventsFactory.setDefaults(config);
            }
            return this;
        }
    }
}
