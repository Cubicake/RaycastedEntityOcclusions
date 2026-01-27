package games.cubi.raycastedAntiESP.config.visibility;

import games.cubi.raycastedAntiESP.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum VisibilityHandlerMode implements ConfigEnum {
    BUKKIT("bukkit"),
    PACKETEVENTS("packetevents");

    private final String pathName;

    VisibilityHandlerMode(String pathName) {
        this.pathName = pathName;
    }

    public String getName() {
        return pathName;
    }

    public String getPathName() {
        return "." + pathName;
    }

    public static @Nullable VisibilityHandlerMode fromString(String name) {
        for (VisibilityHandlerMode mode : values()) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    private static final String[] namesCache;
    static {
        namesCache = Arrays.stream(values()).map(VisibilityHandlerMode::getName).toArray(String[]::new);
    }
    {
        register();
    }

    @Override
    public String[] getValues() {
        return namesCache;
    }
}
