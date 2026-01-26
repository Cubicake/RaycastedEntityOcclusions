package games.cubi.raycastedAntiESP.config.snapshot.block;

public class UnsafeAsyncBukkit extends BukkitBlockSnapshotConfig {
    public UnsafeAsyncBukkit(int refreshRateTicks) {
        super(refreshRateTicks);
    }
    public Mode getMode() {
        return Mode.UNSAFE_ASYNC_BUKKIT;
    }
}
