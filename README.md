Latest version: v1.6.3

The latest version can currently only be found on Modrinth https://modrinth.com/plugin/raycasted-entity-occlusions/

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
