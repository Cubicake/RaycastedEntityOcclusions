package games.cubi.raycastedantiesp.core.snapshot.block.packet;

import java.util.UUID;

public record Chunk(UUID world, int chunkX, int chunkZ) {}
