package games.cubi.raycastedantiesp.paper.utils;

import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class PaperListener implements Listener {
    public PaperListener() {
        Bukkit.getPluginManager().registerEvents(this, RaycastedAntiESP.get());
    }
}
