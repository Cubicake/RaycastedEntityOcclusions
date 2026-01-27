package games.cubi.raycastedAntiESP.deletioncandidates;

@Deprecated(forRemoval = true)
public class BlockSnapshotConfigFactory/* implements ConfigFactory<BlockSnapshotConfig> */{/*
    public static final String PATH = ".block";

    @Override
    public String getFullPath() {
        return SnapshotConfigFactory.PATH + PATH;
    }

    @Override
    public @NotNull BlockSnapshotConfig getFromConfig(FileConfiguration config, BlockSnapshotConfig defaults) {
        Mode mode = getModeFromConfig(config);
        if (mode == null) {
            Logger.warning("Invalid block snapshot mode in config, defaulting to " + defaults.getMode().getName(), 3);
            mode = defaults.getMode();
        }

        return switch (mode) {
            case SYNC_BUKKIT, UNSAFE_ASYNC_BUKKIT ->
                    new BukkitBlockSnapshotConfig.Factory(mode).getFromConfig(config, (BukkitBlockSnapshotConfig) defaults);
            case PACKETEVENTS ->
                    throw new UnsupportedOperationException("PacketEvents block snapshot mode is not yet implemented.");
            default -> {
                Logger.error(new RuntimeException("Unsupported block snapshot mode enum value: " + mode + ", falling back on sync-bukkit"), 3);
                yield new BukkitBlockSnapshotConfig.Factory(Mode.SYNC_BUKKIT).getFromConfig(config, (BukkitBlockSnapshotConfig) defaults);
            }
        };
    }

    private @Nullable Mode getModeFromConfig(FileConfiguration config) {
        String modeName = config.getString(getFullPath()+".mode", Mode.SYNC_BUKKIT.getName());
        return Mode.fromString(modeName);
    }

    @Override
    public ConfigFactory<BlockSnapshotConfig> setDefaults(FileConfiguration config, BlockSnapshotConfig defaults) {
        // Save the mode
        config.addDefault(getFullPath()+".mode", defaults.getMode().getName());
        new BukkitBlockSnapshotConfig.Factory(null).setDefaults(config, (BukkitBlockSnapshotConfig) defaults);
        return this;
    }*/
}
