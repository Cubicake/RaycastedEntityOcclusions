package games.cubi.raycastedantiesp.packetevents;

public interface BlockInfoResolver {
    boolean isOccluding(int blockStateID);
    boolean isTileEntity(int blockStateID);
    boolean isInitialised();
}
