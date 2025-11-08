package games.cubi.raycastedAntiESP.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PacketProcessor {
    private final RaycastedAntiESP plugin;
    private final UUID key;

    public PacketProcessor(RaycastedAntiESP plugin) {
        this.plugin = plugin;
        key = UUID.randomUUID();
    }

    public void processPlayerInfoRemovePacket(PacketSendEvent event) {
        WrapperPlayServerPlayerInfoRemove removePacket = new WrapperPlayServerPlayerInfoRemove(event);
        List<UUID> playersBeingRemoved = removePacket.getProfileIds();
        if (playersBeingRemoved.size() != 2) {
            event.setCancelled(true);
            return;
        }
        if (!playersBeingRemoved.contains(key)) {
            event.setCancelled(true);
        }
    }
    public void sendPlayerInfoRemovePacket(UUID uuid) {
        WrapperPlayServerPlayerInfoRemove removePacket = new WrapperPlayServerPlayerInfoRemove(uuid, key);
        PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerManager.sendPacket(player, removePacket);
        }
    }
}
