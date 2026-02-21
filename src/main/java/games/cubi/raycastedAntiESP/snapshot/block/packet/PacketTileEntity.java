package games.cubi.raycastedAntiESP.snapshot.block.packet;

import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;

public record PacketTileEntity<T>(BlockLocation location, int entityID, T nbt) {}
