package games.cubi.raycastedAntiESP.Packets;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;

public class PacketsListener implements PacketListener {
    private RaycastedAntiESP plugin;
    private PacketProcessor packetProcessor;
    public PacketsListener(RaycastedAntiESP plugin) {
        // This is run on load, not on enable.
        this.plugin = plugin;

    }
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            initializePacketProcessor();
            packetProcessor.processPlayerInfoRemovePacket(event);
        }
    }

    private void initializePacketProcessor() {
        if (packetProcessor == null) {
            packetProcessor = RaycastedAntiESP.getPacketProcessor();
        }
    }


}
