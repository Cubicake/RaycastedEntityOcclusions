package games.cubi.raycastedantiesp.core.players;

import games.cubi.locatables.Locatable;
import games.cubi.locatables.implementations.ThreadSafeLocatable;
import games.cubi.raycastedantiesp.core.view.BlockView;
import games.cubi.raycastedantiesp.core.view.EntityView;
import games.cubi.raycastedantiesp.core.view.ViewRegistry;

import java.util.UUID;

public class PlayerData {
    private final UUID playerUUID;
    private final int joinTick;
    private volatile boolean hasBypassPermission;
    private final ThreadSafeLocatable ownLocation;

    private final BlockView blockView;
    private final EntityView<?> entityView;
    private final EntityView<?> playerView;

    public PlayerData(UUID player, boolean hasBypassPermission, int joinTick) {
        this.joinTick = joinTick;
        this.playerUUID = player;
        this.hasBypassPermission = hasBypassPermission;

        blockView = ViewRegistry.createBlockView();
        entityView = ViewRegistry.createEntityView();
        playerView = ViewRegistry.createEntityView();
        ownLocation = new ThreadSafeLocatable(null, 0, 0, 0);
    }

    public EntityView<?> entityView() {
        return entityView;
    }

    public EntityView<?> playerView() {
        return playerView;
    }

    public BlockView blockView() {
        return blockView;
    }

    public void updateOwnLocation(UUID world, double x, double y, double z) {
        ownLocation.set(x, y, z, world);
    }

    public Locatable ownLocation() {
        ThreadSafeLocatable existing = ownLocation;
        return existing == null ? null : existing.clonePlainAndCentreIfBlockLocation();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean hasBypassPermission() {
        return hasBypassPermission;
    }

    public int getJoinTick() {
        return joinTick;
    }

    public void setBypassPermission(boolean hasBypassPermission) {
        this.hasBypassPermission = hasBypassPermission;
    } //todo: need to link up
}
