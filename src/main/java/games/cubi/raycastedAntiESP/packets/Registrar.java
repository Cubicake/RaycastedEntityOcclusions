package games.cubi.raycastedAntiESP.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;

public class Registrar {

    public Registrar(RaycastedAntiESP plugin) {
        // Register the packet listener
        PacketEvents.getAPI().getEventManager().registerListener(new PacketsListener(plugin), PacketListenerPriority.NORMAL);
    }
}
