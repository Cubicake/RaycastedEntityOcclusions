package games.cubi.raycastedantiesp.paper;

import games.cubi.locatables.Locatable;
import games.cubi.raycastedantiesp.core.raycast.ParticleSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;

import java.util.Objects;

public class PaperParticleSpawner implements ParticleSpawner {
    public void spawnParticleAt(Locatable locatable, Colour colour) {
        Objects.requireNonNull(Bukkit.getWorld(locatable.world())).spawnParticle(Particle.DUST, locatable.x(), locatable.y(), locatable.z(), 0);
    }

    private static final Particle.DustOptions RED_DUST = new Particle.DustOptions(Color.RED, 1);
    private static final Particle.DustOptions GREEN_DUST = new Particle.DustOptions(Color.GREEN, 1);
    private static final Particle.DustOptions BLUE_DUST = new Particle.DustOptions(Color.BLUE, 1);

    private static Particle.DustOptions toBukkitDust(Colour colour) {
        return switch (colour) {
            case RED -> RED_DUST;
            case GREEN -> GREEN_DUST;
            case BLUE -> BLUE_DUST;
        };
    }
}
