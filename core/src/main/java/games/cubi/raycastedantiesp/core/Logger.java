package games.cubi.raycastedantiesp.core;

import games.cubi.logs.PlatformLogger;

// Literally just an alias for Core.logger
public class Logger {
    public static PlatformLogger get() {
        return Core.logger;
    }
}
