package games.cubi.raycastedantiesp.paper.visibilitychangehandlers.player;

import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.players.VisibilityTracker;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.PlayerVisibilityChanger;
import games.cubi.raycastedantiesp.paper.data.DataHolder;
import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.BukkitAbstractVisibilityChanger;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.UUID;

public class BukkitPVC extends BukkitAbstractVisibilityChanger implements PlayerVisibilityChanger {

    public BukkitPVC() {
        super();
    }

    @Override
    protected VisibilityTracker<UUID> getCorrectVisibilityTracker(UUID playerUUID) {
        return PlayerRegistry.getInstance().getPlayerData(playerUUID).playerVisibility();
    }
    @Override
    public void showPlayerToPlayer(UUID player, UUID otherPlayer, int currentTick) {
        super.showAbstractEntityToPlayer(player, otherPlayer, currentTick);
    }

    @Override
    public void hidePlayerFromPlayer(UUID player, UUID otherPlayer, int currentTick) {
        super.hideAbstractEntityFromPlayer(player, otherPlayer, currentTick);
    }

    @Override
    public void processCache() {
        processCaches();
    }

    @Override
    public VisibilityChangeHandlers.PlayerVisibilityChangerType getType() {
        return VisibilityChangeHandlers.PlayerVisibilityChangerType.BUKKIT;
    }

    //Listeners

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTrackEntity(PlayerTrackEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(playerUUID);

        int currentTick = DataHolder.getTick();

        if (playerData == null || playerData.playerVisibility().isVisible(entityUUID, currentTick)) return; // The player is meant to see the entity, do nothing

        hidePlayerFromPlayer(playerUUID, entityUUID, currentTick);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerUntrackEntity(PlayerUntrackEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(playerUUID);
        final int currentTick = DataHolder.getTick();

        if ((playerData == null) || (!playerData.playerVisibility().isVisible(entityUUID, currentTick))) return; // State change was triggered by us, do nothing

        playerData.playerVisibility().remove(entityUUID); // Remove entity from player's data as they are no longer tracking it, so no more raycasts are needed
    }
}
