package games.cubi.raycastedAntiESP.snapshot.block.packet;

import java.util.UUID;

public record Chunk(UUID world, int chunkX, int chunkZ) {}
