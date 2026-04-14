package games.cubi.locatables;

import java.util.UUID;

public sealed interface ChunkLocatable permits ChunkLocatable.ImmutableChunkLocatable, Locatable {
    UUID world();
    int chunkX();
    int chunkZ();

    record ImmutableChunkLocatable(UUID world, int chunkX, int chunkZ) implements ChunkLocatable {
        public ImmutableChunkLocatable(Locatable locatable) {
            this(locatable.world(), locatable.chunkX(), locatable.chunkZ());
        }
    }
}