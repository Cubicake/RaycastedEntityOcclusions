package games.cubi.raycastedantiesp.packetevents.viewcontrollers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;

import java.util.UUID;
import java.util.function.IntSupplier;

public class PacketEventsCommonViewController {
    private static PacketEventsCommonViewController INSTANCE;
    private final  IntSupplier currentTickSupplier;
    public final boolean v_1_21_5_orAbove = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5);

    private PacketEventsCommonViewController(IntSupplier currentTick) {
        this.currentTickSupplier = currentTick;
    }

    public static PacketEventsCommonViewController get(IntSupplier currentTick) {
        if (INSTANCE == null) {
            INSTANCE = new PacketEventsCommonViewController(currentTick);
        }
        return INSTANCE;
    }

    public PlayerData ensurePlayerData(UUID viewerUUID, PacketSendEvent event) {
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(viewerUUID);
        if (playerData != null) {
            return playerData;
        }

        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            PlayerRegistry.getInstance().registerPlayer(viewerUUID, false, currentTickSupplier.getAsInt());
            return PlayerRegistry.getInstance().getPlayerData(viewerUUID);
        }
        return null;
    }

    public void writeIfPresent(User viewer, PacketWrapper<?> packet) {
        if (viewer == null || packet == null) {
            return;
        }

        viewer.writePacketSilently(packet);
    }
}
