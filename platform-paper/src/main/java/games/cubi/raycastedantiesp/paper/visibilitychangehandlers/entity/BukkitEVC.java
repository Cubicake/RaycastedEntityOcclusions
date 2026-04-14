package games.cubi.raycastedantiesp.paper.visibilitychangehandlers.entity;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.players.VisibilityTracker;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.EntityVisibilityChanger;
import games.cubi.raycastedantiesp.paper.data.DataHolder;
import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.BukkitAbstractVisibilityChanger;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.UUID;

public class BukkitEVC extends BukkitAbstractVisibilityChanger implements EntityVisibilityChanger {

    public BukkitEVC() {
        super();
    }

    @Override
    protected VisibilityTracker<UUID> getCorrectVisibilityTracker(UUID playerUUID) {
        return PlayerRegistry.getInstance().getPlayerData(playerUUID).entityVisibility();
    }

    @Override
    public void showEntityToPlayer(UUID player, UUID entity, int currentTick) {
        if (!PlayerRegistry.getInstance().isPlayerRegistered(player)) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3, BukkitEVC.class);
            return;
        }
        super.showAbstractEntityToPlayer(player, entity, currentTick);
    }

    @Override
    public void hideEntityFromPlayer(UUID player, UUID entity, int currentTick) {
        if (!PlayerRegistry.getInstance().isPlayerRegistered(player)) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3, BukkitEVC.class);
            return;
        }
        super.hideAbstractEntityFromPlayer(player, entity, currentTick);
    }

    @Override
    public VisibilityChangeHandlers.EntityVisibilityChangerType getType() {
        return VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT;
    }

    @Override
    public void processCache() {
        processCaches();
    }

    //Listeners

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTrackEntity(PlayerTrackEntityEvent event) {
        if (event.getEntity() instanceof Player) return;

        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(playerUUID);

        int currentTick = DataHolder.getTick();

        if (playerData == null || playerData.entityVisibility().isVisible(entityUUID, currentTick)) return; // The player is meant to see the entity, do nothing

        hideEntityFromPlayer(playerUUID, entityUUID, currentTick);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerUntrackEntity(PlayerUntrackEntityEvent event) {
        if (event.getEntity() instanceof Player) return;

        UUID entityUUID = event.getEntity().getUniqueId();
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(playerUUID);
        final int currentTick = DataHolder.getTick();

        if ((playerData == null) || (!playerData.entityVisibility().isVisible(entityUUID, currentTick))) return; // State change was triggered by us, do nothing

        playerData.entityVisibility().remove(entityUUID); // Remove entity from player's data as they are no longer tracking it, so no more raycasts are needed
    }
}
