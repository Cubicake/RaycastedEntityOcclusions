package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.Config;
import games.cubi.raycastedAntiESP.config.ConfigFactory;
import org.bukkit.configuration.file.FileConfiguration;
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
        public @NotNull EntityVisibilityHandlerConfig getFromConfig(FileConfiguration config,
                                                                    @Nullable EntityVisibilityHandlerConfig defaults) {
            EntityVisibilityHandlerConfig fallback = defaults != null ? defaults : DEFAULT;
            EntityVisibilityHandlerMode mode = readMode(config, fallback);
            return switch (mode) {
                case BUKKIT -> bukkitFactory.getFromConfig(config, BukkitEntityVisibilityHandlerConfig.DEFAULT);
                case PACKETEVENTS -> packetEventsFactory.getFromConfig(
                    config,
                    PacketEventsEntityVisibilityHandlerConfig.DEFAULT
                );
            };
        }

        private EntityVisibilityHandlerMode readMode(FileConfiguration config, EntityVisibilityHandlerConfig fallback) {
            String modeName = config.getString(getFullPath() + ".mode", fallback.getName());
            EntityVisibilityHandlerMode mode = EntityVisibilityHandlerMode.fromString(modeName);
            if (mode == null) {
                Logger.warning("Invalid entity visibility handler mode in config, defaulting to " + fallback.getName(), 3);
                mode = fallback.getMode();
            }
            return mode;
        }

        @Override
        public @NotNull ConfigFactory<EntityVisibilityHandlerConfig> setDefaults(FileConfiguration config,
                                                                                 @Nullable EntityVisibilityHandlerConfig defaults) {
            EntityVisibilityHandlerConfig fallback = defaults != null ? defaults : DEFAULT;
            config.addDefault(getFullPath() + ".mode", fallback.getName());
            EntityVisibilityHandlerMode mode = fallback.getMode();
            switch (mode) {
                case BUKKIT -> bukkitFactory.setDefaults(config, BukkitEntityVisibilityHandlerConfig.DEFAULT);
                case PACKETEVENTS -> packetEventsFactory.setDefaults(
                    config,
                    PacketEventsEntityVisibilityHandlerConfig.DEFAULT
                );
            }
            return this;
        }
    }
}
