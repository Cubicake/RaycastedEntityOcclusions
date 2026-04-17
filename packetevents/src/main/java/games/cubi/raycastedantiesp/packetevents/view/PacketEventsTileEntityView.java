package games.cubi.raycastedantiesp.packetevents.view;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.minecraft.TileEntityLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.locatables.minecraft.NettyTileEntity;
import games.cubi.raycastedantiesp.core.view.TileEntityView;
import games.cubi.raycastedantiesp.core.view.TileEntityViewTransition;
import games.cubi.raycastedantiesp.packetevents.locatables.PacketEventsTileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketEventsTileEntityView implements TileEntityView {
    private final Map<ImmutableBlockLocatable, PacketEventsTileEntity> knownTileEntities = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<TileEntityViewTransition> transitions = new ConcurrentLinkedQueue<>();

    @Override
    public void upsertTileEntity(BlockLocatable location, int currentTick) {
        ImmutableBlockLocatable key = toImmutable(location);
        knownTileEntities.compute(key, (ignored, existing) ->
                existing == null ? new PacketEventsTileEntity(key, false, currentTick) : (PacketEventsTileEntity) existing.setLastChecked(currentTick));
    }

    @Override
    public void insertIfAbsent(BlockLocatable location) {
        ImmutableBlockLocatable key = toImmutable(location);
        knownTileEntities.computeIfAbsent(key, ignored -> new PacketEventsTileEntity(key, false, 0));
    }

    @Override
    public void removeTileEntity(BlockLocatable location) {
        ImmutableBlockLocatable key = toImmutable(location);
        if (knownTileEntities.remove(key) != null) {
            transitions.add(new TileEntityViewTransition(TileEntityViewTransition.Type.FORGET, key));
        }
    }

    @Override
    public TileEntityLocatable<?> getTrackedTileEntity(BlockLocatable location) {
        return knownTileEntities.get(toImmutable(location));
    }

    @Override
    public TileEntityLocatable<?> getTrackedTileEntity(ImmutableBlockLocatable location) {
        return knownTileEntities.get(location);
    }

    @Override
    public boolean isVisible(BlockLocatable location, int currentTick) {
        PacketEventsTileEntity state = knownTileEntities.get(toImmutable(location));
        return state == null || state.visible();
    }

    @Override
    public void setVisibility(BlockLocatable location, boolean visible, int currentTick) {
        ImmutableBlockLocatable key = toImmutable(location);
        PacketEventsTileEntity existing = knownTileEntities.get(key);
        if (existing == null) {
            return;
        }
        if (existing.visible() != visible) {
            transitions.add(new TileEntityViewTransition(
                    visible ? TileEntityViewTransition.Type.SHOW : TileEntityViewTransition.Type.HIDE,
                    key
            ));
        }
        existing.setVisible(visible);
        existing.setLastChecked(currentTick);
    }

    @Override
    public Collection<BlockLocatable> getKnownTileEntities() {
        return List.copyOf(knownTileEntities.keySet());
    }

    @Override
    public Collection<BlockLocatable> getNeedingRecheck(int recheckTicks, int currentTick) {
        List<BlockLocatable> needingRecheck = new ArrayList<>();
        for (Map.Entry<ImmutableBlockLocatable, PacketEventsTileEntity> entry : knownTileEntities.entrySet()) {
            PacketEventsTileEntity state = entry.getValue();
            if (state.visible() && currentTick - state.lastChecked() < recheckTicks) {
                continue;
            }
            needingRecheck.add(entry.getKey());
        }
        return needingRecheck;
    }

    @Override
    public boolean hasPendingTransitions() {
        return !transitions.isEmpty();
    }

    @Override
    public List<TileEntityViewTransition> drainTransitions() {
        List<TileEntityViewTransition> drained = new ArrayList<>();
        TileEntityViewTransition transition;
        while ((transition = transitions.poll()) != null) {
            drained.add(transition);
        }
        return drained;
    }

    private ImmutableBlockLocatable toImmutable(BlockLocatable location) {
        return new ImmutableBlockLocatable(location.world(), location.blockX(), location.blockY(), location.blockZ());
    }
}
