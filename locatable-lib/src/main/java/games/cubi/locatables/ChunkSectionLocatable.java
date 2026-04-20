package games.cubi.locatables;

import java.util.UUID;

public sealed interface ChunkSectionLocatable extends ChunkLocatable permits ChunkSectionLocatable.ImmutableChunkSectionLocatable, Locatable {
    int chunkY();

    record ImmutableChunkSectionLocatable(UUID world, int chunkX, int chunkY, int chunkZ) implements ChunkSectionLocatable {
        public ImmutableChunkSectionLocatable(Locatable locatable) {
            this(locatable.world(), locatable.chunkX(), locatable.chunkY(), locatable.chunkZ());
        }
    }
}
