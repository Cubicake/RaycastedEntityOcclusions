package games.cubi.raycastedAntiESP.config.engine;

import games.cubi.raycastedAntiESP.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum EngineMode implements ConfigEnum {
    SIMPLE("simple"),
    PREDICTIVE("predictive");

    private final String pathName;

    EngineMode(String pathName) {
        this.pathName = pathName;
    }

    public String getName() {
        return pathName;
    }

    public String getPathName() {
        return "." + pathName;
    }

    public static @Nullable EngineMode fromString(String name) {
        for (EngineMode mode : values()) {
            if (mode.getName().equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    private static final String[] namesCache;
    static {
        namesCache = Arrays.stream(values()).map(EngineMode::getName).toArray(String[]::new);
    }
    {
        register();
    }

    @Override
    public String[] getValues() {
        return namesCache;
    }
}
