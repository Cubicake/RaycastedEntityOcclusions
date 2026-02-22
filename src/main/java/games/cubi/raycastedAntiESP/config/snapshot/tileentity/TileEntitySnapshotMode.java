package games.cubi.raycastedAntiESP.config.snapshot.tileentity;

import games.cubi.raycastedAntiESP.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum TileEntitySnapshotMode implements ConfigEnum {
    BUKKIT("bukkit"),
    PACKETEVENTS("packetevents");

    private final String pathName;

    TileEntitySnapshotMode(String pathName) {
        this.pathName = pathName;
    }

    public String getName() {
        return pathName;
    }

    public String getPathName() {
        return "." + pathName;
    }

    public static @Nullable TileEntitySnapshotMode fromString(String name) {
        for (TileEntitySnapshotMode mode : values()) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    private static final String[] namesCache;
    static {
        namesCache = Arrays.stream(values()).map(TileEntitySnapshotMode::getName).toArray(String[]::new);
    }
    {
        register();
    }

    @Override
    public String[] getValues() {
        return namesCache;
    }
}
