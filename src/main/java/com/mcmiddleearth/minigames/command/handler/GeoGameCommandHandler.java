package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GeoGame;
import com.mcmiddleearth.minigames.util.Permission;
import com.mcmiddleearth.minigames.util.PluginData;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class GeoGameCommandHandler {

    GameType type = GameType.GEO_GUESSR;

    public GeoGameCommandHandler(){}

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){
        helpfulLiteralBuilder
                .then(HelpfulLiteralBuilder.literal("blacklist")
                        .withHelpText("Deactivates Warps in GeoGuessr.")
                        .withTooltip("/game blacklist delete <number in list> (seeable through show).")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER))
                        .then(HelpfulLiteralBuilder.literal("add")
                                .then(HelpfulRequiredArgumentBuilder.argument("warp",greedyString())
                                .executes(context -> doCommand(context.getSource(), "blacklistAdd",context.getArgument("warp",String.class)))))
                        .then(HelpfulLiteralBuilder.literal("delete")
                                .then(HelpfulRequiredArgumentBuilder.argument("warp",greedyString())
                                        .executes(context -> doCommand(context.getSource(), "blacklistDelete", context.getArgument("warp",String.class)))))
                        .then(HelpfulLiteralBuilder.literal("show")
                                .executes(context -> doCommand(context.getSource(), "blacklistShow",null))))
                .then(HelpfulLiteralBuilder.literal("restore")
                        .withHelpText("Restores signs.")
                        .withTooltip("Restores signs if the server crashes and the game cant do it itself. Stores in a yml file.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.STAFF))
                        .executes(context -> doCommand(context.getSource(), "restore", null)))
                .then(HelpfulLiteralBuilder.literal("round")
                        .withHelpText("TPs to next warp.")
                        .withTooltip("Teleports all game participants to the next Warp.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("radius",integer())
                                .then(HelpfulRequiredArgumentBuilder.argument("time",integer())
                                        .executes(context -> doCommand(context.getSource(), "round",String.valueOf(context.getArgument("radius",Integer.class)),String.valueOf(context.getArgument("time",Integer.class)))))))
                .then(HelpfulLiteralBuilder.literal("setarea")
                        .withHelpText("Sets a certain game area.")
                        .withTooltip("sets a certain game area. Default is all.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("area",word())
                                .executes(context -> doCommand(context.getSource(), "setarea", context.getArgument("area",String.class)))))
                .then(HelpfulLiteralBuilder.literal("setrounds")
                        .withHelpText("Sets the number of rounds.")
                        .withTooltip("Sets the number of rounds.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("rounds",integer())
                                .executes(context -> doCommand(context.getSource(), "setrounds", String.valueOf(context.getArgument("rounds",Integer.class))))));
        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        GeoGame geoGame;

        switch (command){
            case "blacklistAdd":
                sendNotImplementedYetMessage(sender);
                break;
            case "blacklistDelete":
                sendNotImplementedYetMessage(sender);
                break;
            case "blacklistShow":
                sendNotImplementedYetMessage(sender);
                break;
            case "restore":
                sendNotImplementedYetMessage(sender);
                break;
            case "round":
                sendNotImplementedYetMessage(sender);
                break;
            case "setarea":
                sendNotImplementedYetMessage(sender);
                break;
            case "setrounds":
                sendNotImplementedYetMessage(sender);
                break;
            default:
                sendNothereMessage(sender);
        }
        return 0;
    }

    private void sendNothereMessage(McmeCommandSender sender){
//        PluginData.getMessageUtil().sendErrorMessage(sender,"You shouldn't be here. Try Again!");
    }

    private void sendNotImplementedYetMessage(McmeCommandSender sender){
//        PluginData.getMessageUtil().sendErrorMessage(sender,"This is not yet implemented.");
    }
}
