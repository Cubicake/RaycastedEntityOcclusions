package games.cubi.raycastedAntiESP.config.snapshot.block;

import games.cubi.raycastedAntiESP.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum BlockMode implements ConfigEnum {
    UNSAFE_ASYNC_BUKKIT("unsafe-async-bukkit"),
    PACKETEVENTS("packetevents"),
    SYNC_BUKKIT("sync-bukkit");

    private final String pathName;

    BlockMode(String pathName) {
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

    public static @Nullable BlockMode fromString(String name) {
        for (BlockMode mode : values()) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    private static final String[] namesCache;
    static {
        namesCache = Arrays.stream(values()).map(BlockMode::getName).toArray(String[]::new);
    }
    {
        register();
    }
    @Override
    public String[] getValues() {
        return namesCache;
    }

}
