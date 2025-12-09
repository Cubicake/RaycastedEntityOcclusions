package games.cubi.raycastedAntiESP.deletioncandidates;

public class QuantisedLocation {/*
    // One decimal place quantisation. This means that the coordinates are rounded to the nearest 0.1 block, and since integers are used this works up to 200m blocks away (integer limit/10)
    // Note that the last digit of the integer is the tenths place, so 123456789 represents 12345678.9 in real coordinates.
    private final int rawX;
    private final int rawY;
    private final int rawZ;
    private final UUID world;

    public QuantisedLocation(Location location) {
        this.rawX = (int) Math.floor(location.getX() * 10);
        this.rawY = (int) Math.floor(location.getY() * 10);
        this.rawZ = (int) Math.floor(location.getZ() * 10);
        this.world = location.getWorld().getUID();
    }

    public QuantisedLocation(UUID world, double x, double y, double z) {
        this.rawX = (int) Math.floor(x * 10);
        this.rawY = (int) Math.floor(y * 10);
        this.rawZ = (int) Math.floor(z * 10);
        this.world = world;
    }

    public QuantisedLocation(Location location, double height) {
        this.rawX = (int) Math.floor(location.getX() * 10);
        this.rawY = (int) Math.floor(location.getY()+(height/2) * 10);
        this.rawZ = (int) Math.floor(location.getZ() * 10);
        this.world = location.getWorld().getUID();
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), (rawX / 10.0)+0.05, (rawY / 10.0)+0.05, (rawZ / 10.0)+0.05);
    }

    public ThreadSafeLocation toThreadSafeLoc() {
        return new ThreadSafeLocation(this);
    }

    public Vector toNewVector() {
        return new Vector((rawX / 10.0)+0.05, (rawY / 10.0)+0.05, (rawZ / 10.0)+0.05);
    }

    public boolean isWithinRadius(QuantisedLocation other, double radius) {
        if (!this.world.equals(other.world)) {
            throw new IllegalArgumentException("Cannot calculate distance between different worlds.");
        }

        int dx = this.rawX - other.rawX;
        int dy = this.rawY - other.rawY;
        int dz = this.rawZ - other.rawZ;

        // Convert radius (in blocks) to squared distance in quantised units (tenths of blocks).
        double radiusSquared = radius * radius * 100; // (radius * 10)²
        int distanceSquared = dx * dx + dy * dy + dz * dz;

        return distanceSquared <= radiusSquared;
    }

    public double realX() {
        return (rawX / 10.0) + 0.05;
    }

    public double realY() {
        return (rawY / 10.0) + 0.05;
    }

    public double realZ() {
        return (rawZ / 10.0) + 0.05;
    }

    public UUID world() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuantisedLocation other)) return false;
        return rawX == other.rawX && rawY == other.rawY && rawZ == other.rawZ && world.equals(other.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, rawX, rawY, rawZ);
    }

    @Override
    public LocatableType getType() {
        return LocatableType.Quantised;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockX() {
        return rawX / 10;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockY() {
        return rawY / 10;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockZ() {
        return rawZ / 10;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double x() {
        return rawX / 10f;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double y() {
        return rawY / 10f;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double z() {
        return rawZ / 10f;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public boolean isBlock() {
        return false;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public boolean isFine() {
        return false;
    }*/
}
