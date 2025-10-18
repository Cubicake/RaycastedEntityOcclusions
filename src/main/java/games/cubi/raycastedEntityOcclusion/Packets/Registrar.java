package games.cubi.raycastedEntityOcclusion.Packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import games.cubi.raycastedEntityOcclusion.RaycastedEntityOcclusion;

public class Registrar {

    public Registrar(RaycastedEntityOcclusion plugin) {
        // Register the packet listener
        PacketEvents.getAPI().getEventManager().registerListener(new PacketsListener(plugin), PacketListenerPriority.NORMAL);
    }
}
