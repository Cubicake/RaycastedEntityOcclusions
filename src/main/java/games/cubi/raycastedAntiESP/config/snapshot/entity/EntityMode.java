package games.cubi.raycastedAntiESP.config.snapshot.entity;

import games.cubi.raycastedAntiESP.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum EntityMode implements ConfigEnum {
    PACKETEVENTS("packetevents"),
    BUKKIT("bukkit");

    private final String pathName;

    EntityMode(String pathName) {
        this.pathName = pathName;
    }
    public String getName() {
        return pathName;
    }

    /**
     * @return The path name with a leading dot (e.g. ".sync-bukkit")
     */
    public String getPathName() {
        return "."+pathName;
    }

    public static @Nullable EntityMode fromString(String name) {
        for (EntityMode mode : values()) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    private static final String[] namesCache;
    static {
        namesCache = Arrays.stream(values()).map(EntityMode::getName).toArray(String[]::new);
    }
    {
        register();
    }
    @Override
    public String[] getValues() {
        return namesCache;
    }

}
