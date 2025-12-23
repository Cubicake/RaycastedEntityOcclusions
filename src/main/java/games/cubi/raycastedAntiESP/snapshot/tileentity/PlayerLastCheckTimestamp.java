package games.cubi.raycastedAntiESP.snapshot.tileentity;

import java.util.UUID;

public class PlayerLastCheckTimestamp {
    private final UUID player;
    private int timestamp;
    private boolean seen;

    public PlayerLastCheckTimestamp(UUID player, int timestamp, boolean seen) {
        this.player = player;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public boolean hasBeenSeen() {
        return seen;
    }

    public UUID getPlayer() {
        return player;
    }

    public void update(int timestamp, boolean seen) {
        this.timestamp = timestamp;
        this.seen = seen;
    }

    @Override
    public String toString() {
        return "PlayerLastCheckTimestamp{" +
                "timestamp=" + timestamp +
                ", seen=" + seen +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof PlayerLastCheckTimestamp that)) return false;
        return player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }
}
