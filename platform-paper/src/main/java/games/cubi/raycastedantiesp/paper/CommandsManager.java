package games.cubi.raycastedantiesp.paper;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.Locatable;
import games.cubi.locatables.MutableLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.locatables.implementations.MutableLocatableImpl;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.raycast.RaycastUtil;
import games.cubi.raycastedantiesp.paper.staging.PacketEventsPaperBlockInfoResolver;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import games.cubi.raycastedantiesp.core.config.ConfigManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class CommandsManager {

    private CommandsManager() {}

    public static LiteralCommandNode<CommandSourceStack> registerCommand(RaycastedAntiESP plugin, ConfigManager config) {
        //run help command if no context provided
        return Commands.literal("raycastedantiesp")
            .requires(sender -> sender.getSender().hasPermission("raycastedantiesp.command"))
            .executes(context -> {
                helpCommand(context, plugin);
                return Command.SINGLE_SUCCESS;
            })
            .then(Commands.literal("help")
                .executes(context -> helpCommand(context, plugin)))
            .then(Commands.literal("reload")
                .executes(context -> {
                    try {
                        config.load();
                        context.getSource().getSender().sendMessage("[RaycastedAntiESP] Config reloaded.");
                    } catch (RuntimeException e) {
                        context.getSource().getSender().sendRichMessage("<red>[RaycastedAntiESP] Config reload rejected: <white>" + e.getMessage());
                    }
                    return Command.SINGLE_SUCCESS;
                }))
            .then(Commands.literal("config-values")
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    //dynamic config values
                    sender.sendMessage("[RaycastedAntiESP] Config values: ");

                    for (var entry : config.getConfigValues().entrySet()) {
                        String path = entry.getKey();
                        Object val = entry.getValue();
                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>" + path + "<gray> = <white>" + val));
                    }
                    return Command.SINGLE_SUCCESS;
                }))

            .then(Commands.literal("set")
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    sender.sendRichMessage("<red>Usage: /raycastedantiesp set <key> <value>");;
                    return 0;
                })
                .then(Commands.argument("key", StringArgumentType.string())
                    .then(Commands.argument("value", StringArgumentType.greedyString())
                        .executes(context -> {
                            CommandSender sender = context.getSource().getSender();
                            String key = StringArgumentType.getString(context, "key");
                            String value = StringArgumentType.getString(context, "value");

                            ConfigManager.SetConfigResult result = config.setConfigValue(key, value);
                            sendConfigMutationResult(sender, result, "Set", key, value);
                            return 0;
                        })
                    )
                )
            )
            .then(Commands.literal("add")
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    sender.sendRichMessage("<red>Usage: /raycastedantiesp add <key> <value>");
                    return 0;
                })
                .then(Commands.argument("key", StringArgumentType.string())
                    .then(Commands.argument("value", StringArgumentType.greedyString())
                        .executes(context -> {
                            CommandSender sender = context.getSource().getSender();
                            String key = StringArgumentType.getString(context, "key");
                            String value = StringArgumentType.getString(context, "value");

                            ConfigManager.SetConfigResult result = config.addConfigListValue(key, value);
                            sendConfigMutationResult(sender, result, "Added", key, value);
                            return 0;
                        })
                    )
                )
            )
            .then(Commands.literal("remove")
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    sender.sendRichMessage("<red>Usage: /raycastedantiesp remove <key> <value>");
                    return 0;
                })
                .then(Commands.argument("key", StringArgumentType.string())
                    .then(Commands.argument("value", StringArgumentType.greedyString())
                        .executes(context -> {
                            CommandSender sender = context.getSource().getSender();
                            String key = StringArgumentType.getString(context, "key");
                            String value = StringArgumentType.getString(context, "value");

                            ConfigManager.SetConfigResult result = config.removeConfigListValue(key, value);
                            sendConfigMutationResult(sender, result, "Removed", key, value);
                            return 0;
                        })
                    )
                )
            )
            .then(Commands.literal("test")
                    .executes(context -> {
                        testCommand(context);
                        return Command.SINGLE_SUCCESS;
                    })
            )
            .then(Commands.literal("check-for-updates")
                    .executes(context -> {
                        CommandSender sender = context.getSource().getSender();
                        UpdateChecker.checkForUpdates(plugin, sender);
                        return Command.SINGLE_SUCCESS;
                    })
            )
            .then(Commands.argument("loc", ArgumentTypes.blockPosition())
                    .executes(commandContext -> {
                        CommandSender sender = commandContext.getSource().getSender();
                        Player player = (Player) sender;
                        final BlockPosition blockPosition = commandContext.getArgument("loc", BlockPositionResolver.class).resolve(commandContext.getSource());
                        BlockLocatable location = new ImmutableBlockLocatable(player.getWorld().getUID(), blockPosition.x(), blockPosition.y(), blockPosition.z());
                        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(player.getUniqueId());
                        sender.sendRichMessage("That block is "+playerData.blockView().isBlockOccluding(location));
                        BlockData data = player.getWorld().getBlockData(new Location(player.getWorld(), blockPosition.x(), blockPosition.y(), blockPosition.z()));
                        WrappedBlockState wrappedData = SpigotConversionUtil.fromBukkitBlockData(data);
                        sender.sendRichMessage("Global ID of that block is "+wrappedData.getGlobalId()+". In Paper terms, that is "+data.getAsString());
                        sender.sendRichMessage("According to PacketEventsPaperBlockInfoResolver, that block is "+ PacketEventsPaperBlockInfoResolver.get.isOccluding(wrappedData.getGlobalId()));
                        sender.sendRichMessage("According to bukkit that block is " +data.getMaterial().isOccluding());
                        return Command.SINGLE_SUCCESS;
                    }))
                .then(Commands.literal("dump").executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    boolean[] occlusionArray = PacketEventsPaperBlockInfoResolver.get.dumpOcclusionArray();
                    // print array in a readable format
                    StringBuilder sb = new StringBuilder();
                    sb.append("Occlusion array: ");
                    for (int y = 0; y < occlusionArray.length; y++) {
                        sb.append("id=").append(y).append(": ").append(occlusionArray[y]).append(",");
                    }
                    sender.sendMessage(sb.toString());

                    boolean[] tileArray = PacketEventsPaperBlockInfoResolver.get.dumpOcclusionArray();
                    // print array in a readable format
                    StringBuilder string2 = new StringBuilder();
                    string2.append("Tile array: ");
                    for (int y = 0; y < occlusionArray.length; y++) {
                        string2.append("id=").append(y).append(": ").append(occlusionArray[y]).append(",");
                    }
                    sender.sendMessage(string2.toString());

                    return Command.SINGLE_SUCCESS;
                }
                ))
                .then(Commands.literal("debug").executes(CommandsManager::debugCommand))
                .then(Commands.literal("print-block-ids").executes((ignored) -> {PacketEventsPaperBlockInfoResolver.get.iterateBlockIDs(true); return Command.SINGLE_SUCCESS;}))
            .build();
    }

    public static int helpCommand(CommandContext<CommandSourceStack> context, RaycastedAntiESP plugin) {
        CommandSender sender = context.getSource().getSender();
        sender.sendRichMessage("<white>RaycastedAntiESP <yellow>v" + plugin.getDescription().getVersion());
        sender.sendRichMessage("<white>Commands:");
        sender.sendRichMessage("<green>/raycastedantiesp reload <gray>- Reloads the config");
        sender.sendRichMessage("<green>/raycastedantiesp config-values <gray>- Shows all config values");
        sender.sendRichMessage("<green>/raycastedantiesp set <key> <value> <gray>- Sets a config value");
        sender.sendRichMessage("<green>/raycastedantiesp add <key> <value> <gray>- Adds a value to a list config");
        sender.sendRichMessage("<green>/raycastedantiesp remove <key> <value> <gray>- Removes a value from a list config");
        return Command.SINGLE_SUCCESS;
    }

    private static void sendConfigMutationResult(CommandSender sender, ConfigManager.SetConfigResult result, String action, String key, String value) {
        if (!result.success()) {
            sender.sendRichMessage("<red>Invalid config change: <white>" + result.message());
            return;
        }
        sender.sendRichMessage("<white>" + action + " <green>" + value + "<white> for <green>" + key);
        if (result.restartRequired()) {
            sender.sendRichMessage("<yellow>This change was saved but requires a restart: <white>" + result.message());
        }
    }

    private static int testCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = (Player) context.getSource().getSender();
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(player.getUniqueId());
        Entity closestEntity = player.getNearbyEntities(10,10,10).getFirst();
        if (closestEntity == null) return -1;
        player.sendRichMessage("Closest entity is "+closestEntity.getName());
        Locatable entityLocatable = playerData.entityView().getLocation(closestEntity.getUniqueId());
        Location bukkitLoc = closestEntity.getLocation().clone();
        player.sendRichMessage("Entity location according to PacketEvents is "+entityLocatable);
        player.sendRichMessage("Entity location according to Bukkit is "+bukkitLoc);
        double driftX = Math.abs(entityLocatable.x() - bukkitLoc.getX());
        double driftZ = Math.abs(entityLocatable.z() - bukkitLoc.getZ());
        if (driftX < 0.001) driftX = 0;
        if (driftZ < 0.001) driftZ = 0;
        Logger.debug("Drift is X: "+driftX+" Z: "+driftZ);
        return Command.SINGLE_SUCCESS;
    }

    private static int debugCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        //benchmark raycast speed by generating 1000 locatables normally distributed approx 50 blocks around the player and raycasting to them, then printing the average time taken

        Locatable[] locatables = new Locatable[1000];
        Player player = (Player) context.getSource().getSender();
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(player.getUniqueId());
        Locatable playerLocatable = playerData.ownLocation();
        MutableLocatable unitDirection = new MutableLocatableImpl(playerLocatable.world(), 0, 0, 0);
        for (int i = 0; i < locatables.length; i++) {
            unitDirection.setX(Math.random() - 0.5);
            unitDirection.setY(Math.random() - 0.5);
            unitDirection.setZ(Math.random() - 0.5);
            unitDirection.normalize();
            unitDirection.scalarMultiply(50);
            locatables[i] = playerLocatable.clonePlainAndCentreIfBlockLocation().add(unitDirection);
        }
        Bukkit.getAsyncScheduler().runNow(RaycastedAntiESP.get(), (ignored) -> {
            long startTime = System.nanoTime();
            for (Locatable locatable : locatables) {
                RaycastUtil.raycast(playerData, playerLocatable, locatable, 3, 0, 100, false, playerData.blockView(), 1, null);
            }
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            double averageTime = duration / (double) locatables.length;
            player.sendRichMessage("Average raycast time: " + averageTime + " nanoseconds");
            player.sendRichMessage("Total raycast time: " + duration + " nanoseconds");
        });


        /*
        Player player = (Player) context.getSource().getSender();
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(player.getUniqueId());
        AbstractBlockView<?> pbsm = (AbstractBlockView<?>) playerData.blockView();
        player.sendMessage(pbsm.loadedChunkCount() +"chunks loaded");*/
        return Command.SINGLE_SUCCESS;
    }
}
