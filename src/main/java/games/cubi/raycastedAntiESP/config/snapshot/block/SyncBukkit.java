package games.cubi.raycastedAntiESP.config.snapshot.block;

public class SyncBukkit extends BukkitBlockSnapshotConfig {
    public SyncBukkit(int refreshRateTicks) {
        super(refreshRateTicks);
    }

    public Mode getMode() {
        return Mode.SYNC_BUKKIT;
    }
}
