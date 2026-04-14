package games.cubi.raycastedantiesp.core.engine;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.Locatable;
import games.cubi.raycastedantiesp.core.config.ConfigManager;
import games.cubi.raycastedantiesp.core.config.DebugConfig;
import games.cubi.raycastedantiesp.core.config.raycast.EntityConfig;
import games.cubi.raycastedantiesp.core.config.raycast.PlatformTileEntityConfig;
import games.cubi.raycastedantiesp.core.config.raycast.PlayerConfig;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.raycast.ParticleSpawner;
import games.cubi.raycastedantiesp.core.raycast.RaycastUtil;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.PlayerEntitySnapshotManager;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.EntityVisibilityChanger;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.PlayerVisibilityChanger;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.TileEntityVisibilityChanger;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class SimpleEngine implements Engine {
    private final ConfigManager config;
    private final ParticleSpawner particleSpawner;
    private final Supplier<Collection<PlayerData>> playerSupplier;
    private final IntSupplier currentTickSupplier;

    public SimpleEngine(ConfigManager config, ParticleSpawner particleSpawner, Supplier<Collection<PlayerData>> playerSupplier, IntSupplier currentTickSupplier) {
        this.config = config;
        this.particleSpawner = particleSpawner;
        this.playerSupplier = playerSupplier;
        this.currentTickSupplier = currentTickSupplier;
    }

    @Override
    public void tick() {
        final int currentTick = currentTickSupplier.getAsInt();
        Collection<PlayerData> allPlayers = playerSupplier.get();
        int threads = 1; //TODO Don't hardcode
        if (threads < 1) threads = 1;

        List<List<PlayerData>> batches = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            batches.add(new ArrayList<>());
        }

        int index = 0;
        for (PlayerData playerData : allPlayers) {
            batches.get(index++ % threads).add(playerData);
        }

        EntityConfig entityConfig = config.getEntityConfig();
        PlayerConfig playerConfig = config.getPlayerConfig();
        PlatformTileEntityConfig<?> tileEntityConfig = config.getTileEntityConfig();
        DebugConfig debugConfig = config.getDebugConfig();

        int tileEntityRadius = (Math.max(tileEntityConfig.getRaycastRadius(), 10) + 15) / 16;

        for (List<PlayerData> batch : batches) {
            processTickForPlayers(batch, entityConfig, playerConfig, tileEntityConfig, tileEntityRadius, debugConfig.showDebugParticles(), currentTick);
        }
    }

    private void processTickForPlayers(List<PlayerData> playerDataList, EntityConfig entityConfig, PlayerConfig playerConfig, PlatformTileEntityConfig<?> tileEntityConfig, int tileEntityRadius,
                                       boolean debugParticles, int currentTick) {

        for (PlayerData playerData : playerDataList) {
            if (playerData.hasBypassPermission()) continue;

            PlayerEntitySnapshotManager entitySnapshotManager = playerData.entitySnapshotManager();
            PlayerBlockSnapshotManager blockSnapshotManager = playerData.blockSnapshotManager();

            Locatable playerLocation = entitySnapshotManager.getLocation(playerData.getPlayerUUID());
            if (playerLocation == null) {
                continue;
            }

            if (entityConfig.isEnabled()) checkEntities(playerData, playerLocation, entityConfig, debugParticles, entitySnapshotManager, blockSnapshotManager, currentTick);
            if (playerConfig.isEnabled()) checkPlayers(playerData, playerLocation, playerConfig, debugParticles, entitySnapshotManager, blockSnapshotManager, currentTick);
            if (tileEntityConfig.isEnabled()) checkTileEntities(playerData, playerLocation, tileEntityConfig, tileEntityRadius, debugParticles, blockSnapshotManager, currentTick);
        }
    }

    private void checkEntities(PlayerData player, Locatable playerLocation, EntityConfig entityConfig, boolean debugParticles, PlayerEntitySnapshotManager entitySnapshotManager, PlayerBlockSnapshotManager blockSnapshotManager, int currentTick) {
        EntityVisibilityChanger entityVisibilityChanger = VisibilityChangeHandlers.getEntity();

        for (UUID entityUUID : player.entityVisibility().getNeedingRecheck(entityConfig.getVisibleRecheckIntervalTicks(), currentTick)) {
            Locatable entityLocation = entitySnapshotManager.getLocation(entityUUID);
            if (entityLocation == null) {
                continue;
            }
            boolean canSee = RaycastUtil.raycast(player, playerLocation, entityLocation, entityConfig.getMaxOccludingCount(), entityConfig.getAlwaysShowRadius(), entityConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1, particleSpawner);
            entityVisibilityChanger.setEntityVisibilityForPlayer(player.getPlayerUUID(), entityUUID, canSee, currentTick);
        }
    }

    private void checkPlayers(PlayerData player, Locatable playerLocation, PlayerConfig playerConfig, boolean debugParticles, PlayerEntitySnapshotManager entitySnapshotManager, PlayerBlockSnapshotManager blockSnapshotManager, int currentTick) {
        PlayerVisibilityChanger playerVisibilityChanger = VisibilityChangeHandlers.getPlayer();

        for (UUID otherPlayerUUID : player.playerVisibility().getNeedingRecheck(playerConfig.getVisibleRecheckIntervalTicks(), currentTick)) {
            Locatable otherPlayerLocation = entitySnapshotManager.getLocation(otherPlayerUUID);
            if (otherPlayerLocation == null) {
                continue;
            }
            boolean canSee = RaycastUtil.raycast(player, playerLocation, otherPlayerLocation, playerConfig.getMaxOccludingCount(), playerConfig.getAlwaysShowRadius(), playerConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1, particleSpawner);
            playerVisibilityChanger.setPlayerVisibilityForPlayer(player.getPlayerUUID(), otherPlayerUUID, canSee, currentTick);
        }
    }

    private void checkTileEntities(PlayerData player, Locatable playerLocation, PlatformTileEntityConfig<?> tileEntityConfig, int chunkRadius, boolean debugParticles, PlayerBlockSnapshotManager blockSnapshotManager, int currentTick) {
        TileEntityVisibilityChanger tileEntityVisibilityChanger = VisibilityChangeHandlers.getTileEntity();
        int chunkX = playerLocation.blockX() >> 4;
        int chunkZ = playerLocation.blockZ() >> 4;

        Set<BlockLocatable> tileEntitiesToCheck = player.tileVisibility().getNeedingRecheck(tileEntityConfig.getVisibleRecheckIntervalTicks(), currentTick, playerLocation.world(), chunkX, chunkZ, chunkRadius);

        for (BlockLocatable tileEntityLocation : tileEntitiesToCheck) {
            if (!player.tileVisibility().containsChunk(tileEntityLocation)) continue;
            boolean canSee = RaycastUtil.raycast(player, playerLocation, tileEntityLocation, tileEntityConfig.getMaxOccludingCount() + 1, tileEntityConfig.getAlwaysShowRadius(), tileEntityConfig.getRaycastRadius(), debugParticles, blockSnapshotManager, 1, particleSpawner);
            tileEntityVisibilityChanger.setTileEntityVisibilityForPlayer(player.getPlayerUUID(), tileEntityLocation, canSee, currentTick);
        }
    }
}
