package games.cubi.raycastedAntiESP.packets;

import games.cubi.raycastedAntiESP.Logger;
import org.jetbrains.annotations.NotNull;

public final class PacketEventsStatus {
    private static volatile PacketEventsStatus instance;
    private final boolean packetEventsPresent;

    private PacketEventsStatus(boolean packetEventsPresent) {
        this.packetEventsPresent = packetEventsPresent;
    }

    public static void init(boolean packetEventsPresent) {
        if (instance != null) {
            Logger.errorAndReturn(new RuntimeException("PacketEventsStatus already initialised"), 1);
            return;
        }
        synchronized (PacketEventsStatus.class) {
            if (instance != null) {
                Logger.errorAndReturn(new RuntimeException("PacketEventsStatus already initialised"), 1);
                return;
            }
            instance = new PacketEventsStatus(packetEventsPresent);
        }
    }

    @NotNull
    public static PacketEventsStatus get() {
        if (instance == null) {
            Logger.error(new RuntimeException("PacketEvents status not initialised"), 1);
            instance = new PacketEventsStatus(false);
        }
        return instance;
    }

    public boolean isPacketEventsPresent() {
        return packetEventsPresent;
    }
}
