package games.cubi.raycastedantiesp.core;

import games.cubi.logs.PlatformLogger;
import games.cubi.logs.Logger;

public class Core {

    public static Core instance;

    private Core(PlatformLogger logger) {
        Logger.init(logger);
    }

    public static Core initialize(PlatformLogger logger) {
        if (instance != null) {
            return instance;
        }
        instance = new Core(logger);
        return instance;
    }

    public static Core getInstance() {
        if (instance == null) {
            Logger.error(new IllegalStateException("Core has not been initialized yet but Core#getInstance called!"),1, Core.class);
        }
        return instance;
    }

    public void intelliJStopThinkingThisIsAUtilClass() {}
}
