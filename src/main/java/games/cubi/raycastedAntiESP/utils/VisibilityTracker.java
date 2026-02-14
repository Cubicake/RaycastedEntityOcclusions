package games.cubi.raycastedAntiESP.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//T may either be a UUID or an AbstractBlockLocation
//Passing tick via method parameter to minimise the number of times it is checked
public abstract class VisibilityTracker<T> {

    protected abstract ConcurrentHashMap<T, PlayerData.VisibilityAndLastCheckTime> getMap();

    public void addMultiple(final Set<T> entities, int currentTick) {
        for (T entityUUID : entities) {
            getMap().putIfAbsent(entityUUID, new PlayerData.VisibilityAndLastCheckTime(false, currentTick)); // Default to hidden if not already present
        }
    }

    public void add(T entity, int currentTick) {
        getMap().putIfAbsent(entity, new PlayerData.VisibilityAndLastCheckTime(false, currentTick)); // Default to hidden if not already present
    }

    public void setVisibility(T entity, boolean visible, int currentTick) {
        getMap().put(entity, new PlayerData.VisibilityAndLastCheckTime(visible, currentTick)); // This can be used for both adding new entries and updating visibility
    }

    public boolean isVisible(T entity, int currentTick) { //todo: does this need to update timestamp?
        return getMap().computeIfAbsent(entity,
                k -> new PlayerData.VisibilityAndLastCheckTime(true, currentTick)) //Default to true as entities are visible unless explicitly hidden TODO: may cause issues?
                .visible();
    }

    public Set<T> getNeedingRecheck(int recheckTicks, int currentTime) {
        Set<T> recheckList = new HashSet<>();

        for (Map.Entry<T, PlayerData.VisibilityAndLastCheckTime> values : getMap().entrySet()) {
            if ((values.getValue().visible()) && (currentTime - values.getValue().lastChecked() < recheckTicks)) continue;
            recheckList.add(values.getKey());
        }

        return recheckList;
    }

    protected Set<T> getNotNeedingRecheck(int recheckTicks, int currentTime) {
        Set<T> notNeedingRecheckList = new HashSet<>();

        for (Map.Entry<T, PlayerData.VisibilityAndLastCheckTime> values : getMap().entrySet()) {
            if ((values.getValue().visible()) && (currentTime - values.getValue().lastChecked() < recheckTicks)) {
                notNeedingRecheckList.add(values.getKey());
            }
        }

        return notNeedingRecheckList;
    }

    public void remove(T entity) {
        getMap().remove(entity);
    }

    /**
     * Atomically sets the visibility of an entity if it differs from the current value.
     * Updates the timestamp even if the visibility remains the same.
     * @param entity A reference to the entity (either UUID or AbstractBlockLocation)
     * @param newVisibility The new visibility state
     * @return true if the visibility was changed, false if it was already set to newVisibility
     */
    public boolean compareAndSetGenericVisibility(T entity, boolean newVisibility, int currentTick) {
        final boolean[] changed = {false};
        getMap().compute(entity, (key, current) -> {

            if ((current == null) || (current.visible() != newVisibility)) {
                changed[0] = true;
            }

            return new PlayerData.VisibilityAndLastCheckTime(newVisibility, currentTick);
        });

        return changed[0];
    }
}
