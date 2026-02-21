package games.cubi.raycastedAntiESP.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static org.bukkit.craftbukkit.block.CraftBlockStates.getBlockEntityType;

public class PacketProcessor {
    private final UUID key = new UUID(0, 0); // Plugins can flag their packets with this UUID;

    public PacketProcessor(RaycastedAntiESP plugin) {
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
    public void processSendTileEntityDataPacket(PacketSendEvent event) {
        WrapperPlayServerBlockEntityData blockEntityDataPacket = new WrapperPlayServerBlockEntityData(event);
        int type = blockEntityDataPacket.getType();
        int nbt = blockEntityDataPacket.getNBT();
    }
}
