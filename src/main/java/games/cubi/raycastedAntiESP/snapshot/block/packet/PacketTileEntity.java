package games.cubi.raycastedAntiESP.snapshot.block.packet;

import games.cubi.raycastedAntiESP.locatables.block.ImmutableBlockLocatable;

public record PacketTileEntity<T>(ImmutableBlockLocatable location, int entityID, T nbt) {}
