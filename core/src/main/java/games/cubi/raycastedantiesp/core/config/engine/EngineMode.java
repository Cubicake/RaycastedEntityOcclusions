package games.cubi.raycastedantiesp.core.config.engine;

import games.cubi.raycastedantiesp.core.config.ConfigEnum;
import org.jetbrains.annotations.Nullable;

public enum EngineMode implements ConfigEnum {
    SIMPLE("simple"),
    NETTY("netty");

    private final String configName;

    EngineMode(String configName) {
        this.configName = configName;
    }

    public String getName() {
        return configName;
    }

    public static @Nullable EngineMode fromString(String name) {
        for (EngineMode mode : values()) {
            if (mode.configName.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    @Override
    public String[] getValues() {
        return new String[] {"simple", "netty"};
    }
}
