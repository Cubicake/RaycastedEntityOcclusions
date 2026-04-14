package games.cubi.raycastedantiesp.paper;

import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.paper.staging.PacketEventsPaperBlockInfoResolver;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import games.cubi.raycastedantiesp.core.config.ConfigManager;
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
                    config.load();
                    context.getSource().getSender().sendMessage("[RaycastedAntiESP] Config reloaded.");
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
                    .then(Commands.argument("value", StringArgumentType.string())
                        .executes(context -> {
                            CommandSender sender = context.getSource().getSender();
                            String key = StringArgumentType.getString(context, "key");
                            String value = StringArgumentType.getString(context, "value");

                            int result = config.setConfigValue(key, value);
                            if (result == -1) {
                                sender.sendRichMessage("<red>Invalid inputs");
                            } else if (result == 0) {
                                //Integer value out of bounds 0 - 256
                                sender.sendRichMessage("<red>Invalid value for <white>" + key + "<red>, must be between 0 and 256");
                            }
                            else {
                                sender.sendRichMessage("<white>Set <green>" + key + "<white> to <green>" + value);
                            }
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
                        sender.sendRichMessage("That block is "+playerData.blockSnapshotManager().isBlockOccluding(location));
                        BlockData data = player.getWorld().getBlockData(new Location(player.getWorld(), blockPosition.x(), blockPosition.y(), blockPosition.z()));
                        WrappedBlockState wrappedData = SpigotConversionUtil.fromBukkitBlockData(data);
                        sender.sendRichMessage("Global ID of that block is "+wrappedData.getGlobalId()+". In Paper terms, that is "+data.getAsString());
                        sender.sendRichMessage("According to PacketEventsPaperBlockInfoResolver, that block is "+ PacketEventsPaperBlockInfoResolver.get.isOccluding(wrappedData.getGlobalId()));
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
            .build();
    }

    public static int helpCommand(CommandContext<CommandSourceStack> context, RaycastedAntiESP plugin) {
        CommandSender sender = context.getSource().getSender();
        sender.sendRichMessage("<white>RaycastedAntiESP <yellow>v" + plugin.getDescription().getVersion());
        sender.sendRichMessage("<white>Commands:");
        sender.sendRichMessage("<green>/raycastedantiesp reload <gray>- Reloads the config");
        sender.sendRichMessage("<green>/raycastedantiesp config-values <gray>- Shows all config values");
        sender.sendRichMessage("<green>/raycastedantiesp set <key> <value> <gray>- Sets a config value");
        return Command.SINGLE_SUCCESS;
    }

    private static void testCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    }
}
