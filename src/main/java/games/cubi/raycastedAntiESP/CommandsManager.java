package games.cubi.raycastedAntiESP;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import games.cubi.raycastedAntiESP.config.ConfigManager;
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

                    ConfigurationSection root = config.getConfigFile().getConfigurationSection("");
                    for (String path : root.getKeys(true)) {
                        Object val = config.getConfigFile().get(path);
                        if (val instanceof ConfigurationSection) continue;
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
            .then(Commands.literal("sync-test")
                    .executes(context -> {
                        Player player = (Player) context.getSource().getSender();
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            player.sendBlockChange(new Location(player.getWorld(), 0, -59, 0), Material.COMMAND_BLOCK.createBlockData());
                        });
                        return Command.SINGLE_SUCCESS;
                    })
            )
                .then(Commands.literal("async-test")
                        .executes(context -> {
                            Player player = (Player) context.getSource().getSender();
                            Bukkit.getAsyncScheduler().runNow(plugin, (scheduledTask) -> {
                                player.sendBlockChange(new Location(player.getWorld(), 0.12839, -59.1238912, 0.2139012), Material.COMMAND_BLOCK.createBlockData());
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                )

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
