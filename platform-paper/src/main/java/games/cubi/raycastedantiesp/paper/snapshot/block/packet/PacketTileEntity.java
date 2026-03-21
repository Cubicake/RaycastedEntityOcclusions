package games.cubi.raycastedantiesp.paper.snapshot.block.packet;

import games.cubi.locatables.implementations.ImmutableBlockLocatable;

public record PacketTileEntity<T>(ImmutableBlockLocatable location, int entityID, T nbt) {}
