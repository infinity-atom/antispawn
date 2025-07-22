package me.lennyd.antispawn.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.lennyd.antispawn.Util;
import me.lennyd.antispawn.antispawn;
import me.lennyd.antispawn.config.ConfigManager;
import me.lennyd.antispawn.config.ConfigRegion;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class MainCommandRegister {

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment registrationEnvironment
    ) {
        dispatcher.register(CommandManager.literal("antispawn")
                .then(CommandManager.literal("info")
                        .executes((commandContext -> {
                            MutableText output = Text.literal("");
                            output.append(Util.formatInfo("antispawn v1.0, by 01lenny\n"));
                            output.append(Util.formatInfo("https://github.com/infinity-atom/antispawn\n"));
                            output.append(Util.formatInfo("https://modrinth.com/project/antispawn\n"));
                            output.append(Util.formatInfo("\n"));
                            output.append(Util.formatInfo("To get started, run /antispawn add <id> <corner1> <corner2>\n"));
                            output.append(Util.formatInfo("This will create a region from <corner1> to <corner2> where no mobs can spawn."));

                            commandContext.getSource().sendMessage(output);

                            return 1;
                        })))
                .then(CommandManager.literal("check")
                        .executes((commandContext -> {
                            if (!commandContext.getSource().isExecutedByPlayer()) {
                                commandContext.getSource().sendMessage(Util.formatError("Only players can execute this command!"));
                                return 1;
                            };

                            for (ConfigRegion region : antispawn.CF.regions) {
                                if (Util.isInsideBox(commandContext.getSource().getPlayer().getBlockPos(),
                                        new BlockPos(region.corner1X, region.corner1Y, region.corner1Z),
                                        new BlockPos(region.corner2X, region.corner2Y, region.corner2Z))) {
                                    commandContext.getSource().sendMessage(Util.formatInfo("You are inside the region " + region.id));
                                    return 1;
                                }
                            }

                            commandContext.getSource().sendMessage(Util.formatError("You weren't detected to be in any region"));

                            return 1;
                        })))
                .then(CommandManager.literal("list")
                        .requires(s -> s.hasPermissionLevel(3))
                        .executes((commandContext -> {
                            MutableText output = Text.literal("");
                            output.append(Util.formatInfo(antispawn.CF.regions.size() + " region(s) found in config\n"));

                            for (ConfigRegion region : antispawn.CF.regions) {
                                output.append(Util.formatInfo(
                                        String.format("%s from %s %s %s to %s %s %s\n",
                                                region.id, region.corner1X, region.corner1Y, region.corner1Z,
                                                region.corner2X, region.corner2Y, region.corner2Z)));
                            }

                            output.append(Util.formatInfo("End of list"));

                            commandContext.getSource().sendFeedback(() -> output, false);
                            return 1;
                        })))
                .then(CommandManager.literal("add")
                        .requires(s -> s.hasPermissionLevel(3))
                        .then(CommandManager.argument("id", StringArgumentType.word())
                            .then(CommandManager.argument("corner1", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("corner2", BlockPosArgumentType.blockPos())
                                    .executes(commandContext -> {
                                        String id = StringArgumentType.getString(commandContext, "id");

                                        for (ConfigRegion region : antispawn.CF.regions) {
                                            if (region.id.equals(id)) {
                                                commandContext.getSource().sendFeedback(() -> Util.formatError("Another region already exists with this ID!"), true);

                                                return 0;
                                            }
                                        }

                                        BlockPos corner1 = BlockPosArgumentType.getBlockPos(commandContext, "corner1");
                                        BlockPos corner2 = BlockPosArgumentType.getBlockPos(commandContext, "corner2");

                                        ConfigRegion r = new ConfigRegion();

                                        r.id = id;
                                        r.corner1X = corner1.getX();
                                        r.corner1Y = corner1.getY();
                                        r.corner1Z = corner1.getZ();

                                        r.corner2X = corner2.getX();
                                        r.corner2Y = corner2.getY();
                                        r.corner2Z = corner2.getZ();

                                        antispawn.CF.regions.add(r);
                                        ConfigManager.save();

                                        commandContext.getSource().sendFeedback(() -> Util.formatInfo("Created region " + id), true);
                                        return 1;
                                    })
                                )
                            )
                        )
                )
                .then(CommandManager.literal("delete")
                        .requires(s -> s.hasPermissionLevel(3))
                        .then(CommandManager.argument("id", StringArgumentType.word())
                                .suggests(((commandContext, suggestionsBuilder) -> {
                                    for (ConfigRegion region : antispawn.CF.regions) {
                                        suggestionsBuilder.suggest(region.id);
                                    }

                                    return suggestionsBuilder.buildFuture();
                                }))
                                .executes((commandContext -> {
                                    String id = StringArgumentType.getString(commandContext, "id");
                                    boolean removed = antispawn.CF.regions.removeIf(r -> r.id.equals(id));

                                    if (removed) {
                                        commandContext.getSource().sendFeedback(() -> Util.formatInfo("Removed region " + id), true);
                                        ConfigManager.save();
                                    } else {
                                        commandContext.getSource().sendFeedback(() -> Util.formatError("Could not remove region " + id), true);
                                    }

                                    return removed ? 1 : 0;
                                }))))
        );
    }
}
