package games.cubi.raycastedAntiESP.packets;

import games.cubi.raycastedAntiESP.Logger;

public final class PacketEventsStatus {
    private static volatile PacketEventsStatus instance;
    private final boolean packetEventsPresent;

    private PacketEventsStatus(boolean packetEventsPresent) {
        this.packetEventsPresent = packetEventsPresent;
    }

    public static void init(boolean packetEventsPresent) {
        if (instance != null) {
            Logger.errorAndReturn(new RuntimeException("PacketEventsStatus already initialised"));
            return;
        }
        synchronized (PacketEventsStatus.class) {
            if (instance != null) {
                Logger.errorAndReturn(new RuntimeException("PacketEventsStatus already initialised"));
                return;
            }
            instance = new PacketEventsStatus(packetEventsPresent);
        }
    }

    public static PacketEventsStatus get() {
        PacketEventsStatus result = instance;
        if (result == null) {
            Logger.errorAndReturn(new RuntimeException("PacketEvents status not initialised"));
            return null;
        }
        return result;
    }

    public boolean isPacketEventsPresent() {
        return packetEventsPresent;
    }
}
