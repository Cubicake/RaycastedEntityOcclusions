package games.cubi.raycastedAntiESP.config.visibility.entity;

import games.cubi.raycastedAntiESP.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum EntityVisibilityHandlerMode implements ConfigEnum {
    BUKKIT("bukkit"),
    PACKETEVENTS("packetevents");

    private final String pathName;

    EntityVisibilityHandlerMode(String pathName) {
        this.pathName = pathName;
    }

    public String getName() {
        return pathName;
    }

    public String getPathName() {
        return "." + pathName;
    }

    public static @Nullable EntityVisibilityHandlerMode fromString(String name) {
        for (EntityVisibilityHandlerMode mode : values()) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    private static final String[] namesCache;
    static {
        namesCache = Arrays.stream(values()).map(EntityVisibilityHandlerMode::getName).toArray(String[]::new);
        values()[0].register();
    }

    @Override
    public String[] getValues() {
        return namesCache;
    }
}
