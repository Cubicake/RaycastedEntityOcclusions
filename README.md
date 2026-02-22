Latest stable version: v1.6.5 | Latest beta version: v2.0.0-beta-1

The latest stable version can currently only be found on Modrinth https://modrinth.com/plugin/raycasted-anti-esp/

The latest beta version can be found on the [discord](https://discord.cubi.games).

This is an async plugin for PaperMC and its forks that hides/culls entities (and tile entities) from players if they do not have line-of-sight.

The supported versions are 1.21.x PaperMC and Pufferfish. Other server versions and software may work too.

Use cases:

- Prevent cheating (anti-esp hacks)
  - Block usage of pie-ray to locate underground bases
  - Prevent mods such as mini-maps or cheat clients from displaying the locations of hidden entities
- Increase client-side performance for low-end devices
  - Massive megabases containing hundreds of armour stands, item frames, banners etc can cause performance issues on low-end devices unable to process so many entities. REO will cull those entities for the client, reducing the number of entities to process.
- Hide nametags behind walls
  - Yes, this plugin is a bit overkill for doing that, yes you can do it anyways.
 
Dependencies:
- Packetevents (soft depend)
  - Only needed if you are using the cull-players option and wish for the players to remain in the tablist

Known issues:
- Due to the nature of the plugin, there will be a short delay once an entity should be visible before it appears, causing it to appear like it "popped" into view. This issue is partially resolved by turning engine-mode to 2, and is worse for players with higher ping.

## Configurate migration plan
To move from Bukkit's `FileConfiguration` to SpongePowered Configurate YAML with minimal disruption, the migration should be staged:

1. **Dependencies & loader**: add `configurate-yaml` + `configurate-core` to Gradle, wire a `YamlConfigurationLoader` that reads/writes `config.yml` in the plugin data folder, and load defaults from the bundled resource.
2. **Config node access**: replace `FileConfiguration` usage in `ConfigManager` with a `ConfigurationNode` root and update `ConfigFactory` to accept nodes instead of Bukkit config objects.
3. **Factory migration**: update each `*Config.Factory` to read values via node lookups (e.g., `node.node("checks", "player", "enabled").getBoolean(default)`), and set defaults by writing to missing nodes.
4. **Reload & set value**: rework `setConfigValue` to validate against node types/enums and persist via Configurate, then reload config objects from the updated node tree.
5. **Compatibility**: on first run after the change, detect existing Bukkit-config values and migrate them into the Configurate tree before saving; log a one-time migration notice.
6. **Cleanup & validation**: remove Bukkit config calls (`saveConfig`, `reloadConfig`, `getConfig`) once all reads/writes are via Configurate, then validate with a server boot test.

## Contributions
Contributions via pull requests are welcome. Please join our [discord](https://discord.cubi.games) to discuss any contributions beforehand to make sure they will not conflict with or hinder existing plans.

You must hold the rights to any code you contribute, and must agree to licence it under the same licence as the rest of the project in perpetuity, explicitly including any and all changes to the project's licence by project maintainers in the future. The project's licence can always be found at [LICENSE.md](LICENSE.md).
