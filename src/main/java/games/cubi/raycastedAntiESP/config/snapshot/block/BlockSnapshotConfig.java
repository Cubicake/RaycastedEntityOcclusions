package games.cubi.raycastedAntiESP.config.snapshot.block;

import games.cubi.raycastedAntiESP.config.Config;

public interface BlockSnapshotConfig extends Config {
    enum Mode {
        UNSAFE_ASYNC_BUKKIT("unsafe-async-bukkit"),
        PACKETEVENTS("packetevents"),
        SYNC_BUKKIT("sync-bukkit");

        private final String pathName;

        Mode(String pathName) {
            this.pathName = pathName;
        }
        public String getPathName() {
            return pathName;
        }
    }
    Mode getMode();

    default String getPathName() {
        return getMode().getPathName();
    }
}
