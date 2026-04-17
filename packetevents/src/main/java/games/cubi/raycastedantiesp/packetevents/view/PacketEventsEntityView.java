package games.cubi.raycastedantiesp.packetevents.view;

import games.cubi.locatables.Locatable;
import games.cubi.locatables.implementations.NettyEntityLocatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.view.EntityView;
import games.cubi.raycastedantiesp.core.view.EntityViewTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketEventsEntityView implements EntityView<NettyEntityLocatable> {
    private final Map<UUID, NettyEntityLocatable> entitiesByUUID = new ConcurrentHashMap<>();
    private final Map<Integer, UUID> entityUUIDsByID = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<EntityViewTransition> transitions = new ConcurrentLinkedQueue<>();

    @Override
    public void insertEntity(NettyEntityLocatable entity) {
        if (entity == null || entity.entityUUID() == null) {
            Logger.error(new RuntimeException("Attempted to insert null entity or entity with null UUID into EntityView"), 2, PacketEventsEntityView.class);
            return;
        }
        entitiesByUUID.put(entity.entityUUID(), entity);
        entityUUIDsByID.put(entity.entityID(), entity.entityUUID());
    }

    @Override
    public void moveRelative(int entityID, double deltaX, double deltaY, double deltaZ, int currentTick) {
        NettyEntityLocatable existing = getTrackedEntity(entityID);
        if (existing == null) {
            return;
        }
        existing.add(deltaX, deltaY, deltaZ);
    }

    @Override
    public void moveAbsolute(int entityID, double x, double y, double z, int currentTick) {
        NettyEntityLocatable existing = getTrackedEntity(entityID);
        if (existing == null) {
            return;
        }
        existing.set(x, y, z, existing.world());
    }

    @Override
    public void removeEntity(int entityID, int currentTick) {
        UUID entityUUID = entityUUIDsByID.remove(entityID);
        if (entityUUID == null) {
            return;
        }
        NettyEntityLocatable removed = entitiesByUUID.remove(entityUUID);
        if (removed != null) {
            transitions.add(new EntityViewTransition(EntityViewTransition.Type.FORGET, removed.entityUUID(), removed.entityID()));
        }
    }

    @Override
    public void removeEntity(UUID entityUUID, int currentTick) {
        int entityID = getEntityID(entityUUID);

        removeEntity(entityID, currentTick);
    }

    @Override
    public NettyEntityLocatable getEntity(UUID entityUUID) {
        return entitiesByUUID.get(entityUUID);
    }

    @Override
    public NettyEntityLocatable getEntity(int entityID) {
        return getTrackedEntity(entityID);
    }

    @Override
    public Locatable getLocation(UUID entityUUID) {
        NettyEntityLocatable entity = entitiesByUUID.get(entityUUID);
        if (entity == null || entity.spawnType() == null) {
            return null;
        }
        return entity.clonePlainAndCentreIfBlockLocation().set(entity.x(), entity.y() + 0.5, entity.z(), entity.world());
    }

    @Override
    public int getEntityID(UUID entityUUID) {
        NettyEntityLocatable entity = entitiesByUUID.get(entityUUID);
        return entity == null ? -1 : entity.entityID();
    }

    @Override
    public boolean isVisible(UUID entityUUID, int currentTick) {
        NettyEntityLocatable entity = entitiesByUUID.get(entityUUID);
        return entity == null || entity.visible();
    }

    @Override
    public void setVisibility(UUID entityUUID, boolean visible, int currentTick) {
        NettyEntityLocatable existing = entitiesByUUID.get(entityUUID);
        if (existing == null) {
            Logger.debug("EntityView.setVisibility missing uuid=" + entityUUID
                    + " requestedVisible=" + visible
                    + " tick=" + currentTick);
            return;
        }
        if (existing.visible() != visible) {
            transitions.add(new EntityViewTransition(
                    visible ? EntityViewTransition.Type.SHOW : EntityViewTransition.Type.HIDE,
                    existing.entityUUID(),
                    existing.entityID()
            ));
        }
        existing.setVisible(visible);
        existing.setLastChecked(currentTick);
    }

    @Override
    public Collection<UUID> getKnownEntities() {
        return List.copyOf(entitiesByUUID.keySet());
    }

    @Override
    public Collection<UUID> getNeedingRecheck(int recheckTicks, int currentTick) {
        List<UUID> needingRecheck = new ArrayList<>();
        for (NettyEntityLocatable state : entitiesByUUID.values()) {
            if (state.spawnType() == null) {
                continue;
            }
            if (state.visible() && (currentTick - state.lastChecked()) < recheckTicks) {
                continue;
            }
            needingRecheck.add(state.entityUUID());
        }
        return needingRecheck;
    }

    @Override
    public boolean hasPendingTransitions() {
        return !transitions.isEmpty();
    }

    @Override
    public List<EntityViewTransition> drainTransitions() {
        List<EntityViewTransition> drained = new ArrayList<>();
        EntityViewTransition transition;
        while ((transition = transitions.poll()) != null) {
            drained.add(transition);
        }
        return drained;
    }

    private NettyEntityLocatable getTrackedEntity(int entityID) {
        UUID entityUUID = entityUUIDsByID.get(entityID);
        return entityUUID == null ? null : entitiesByUUID.get(entityUUID);
    }
}
