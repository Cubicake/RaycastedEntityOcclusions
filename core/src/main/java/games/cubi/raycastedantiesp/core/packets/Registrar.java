package games.cubi.raycastedantiesp.core.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import games.cubi.raycastedantiesp.core.RaycastedAntiESP;

// This only exists because you can't import packetevents api if it doesn't exist, and therefore the main class cant import it
public class Registrar {

    public Registrar(RaycastedAntiESP plugin) {
        // Register the packet listener
        PacketEvents.getAPI().getEventManager().registerListener(new PacketsListener(plugin), PacketListenerPriority.NORMAL);
    }
}
