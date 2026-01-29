package games.cubi.raycastedAntiESP.config.visibility.block;

import games.cubi.raycastedAntiESP.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum BlockVisibilityHandlerMode implements ConfigEnum {
    BUKKIT("bukkit"),
    PACKETEVENTS("packetevents");

    private final String pathName;

    BlockVisibilityHandlerMode(String pathName) {
        this.pathName = pathName;
    }

    public String getName() {
        return pathName;
    }

    public String getPathName() {
        return "." + pathName;
    }

    public static @Nullable BlockVisibilityHandlerMode fromString(String name) {
        for (BlockVisibilityHandlerMode mode : values()) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    private static final String[] namesCache;
    static {
        namesCache = Arrays.stream(values()).map(BlockVisibilityHandlerMode::getName).toArray(String[]::new);
        values()[0].register();
    }

    @Override
    public String[] getValues() {
        return namesCache;
    }
}
