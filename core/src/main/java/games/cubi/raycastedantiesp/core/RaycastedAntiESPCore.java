package games.cubi.raycastedantiesp.core;

import games.cubi.logs.PlatformLogger;
import games.cubi.raycastedantiesp.core.config.raycast.PlatformTileEntityConfig;

import java.io.InputStream;
import java.nio.file.Path;

public class RaycastedAntiESPCore {

    public static PlatformLogger logger;
    public static InputStream resource;
    public static Path dataFolder;
    public static PlatformTileEntityConfig.Factory.FactoryProvider<?> tileEntityConfigFactoryProvider; // This is getting ridiculous lol

    public RaycastedAntiESPCore(PlatformLogger logger, InputStream configFile, Path dataFolder, PlatformTileEntityConfig.Factory.FactoryProvider<?> tileEntityConfigFactoryProvider) {
        RaycastedAntiESPCore.logger = logger;
        RaycastedAntiESPCore.resource = configFile;
        RaycastedAntiESPCore.dataFolder = dataFolder;
        RaycastedAntiESPCore.tileEntityConfigFactoryProvider = tileEntityConfigFactoryProvider;
    }
}
