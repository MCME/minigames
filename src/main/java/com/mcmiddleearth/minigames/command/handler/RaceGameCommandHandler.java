package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.util.Permission;
import com.mcmiddleearth.minigames.util.PluginData;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class RaceGameCommandHandler {

    GameType type = GameType.RACE;

    public RaceGameCommandHandler(){}

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){
        helpfulLiteralBuilder
                .then(HelpfulLiteralBuilder.literal("loadrace")
                        .withHelpText("Loads a race from data file.")
                        .withTooltip("Loads race locations and markers from the file <filename>.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .then(HelpfulRequiredArgumentBuilder.argument("racename",word())
                                .executes(context -> doCommand(context.getSource(), "racename", context.getArgument("racename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("marker")
                        .withHelpText("Assigns a race marker to a checkpoint.")
                        .withTooltip("Appoints the marker from file <filename> to start or finish or checkpoints or all race locations. When only <filename> is specified the marker is assigned to a nearby location (10 blocks).")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .then(HelpfulRequiredArgumentBuilder.argument("racename",word())
                                .then(HelpfulLiteralBuilder.literal("all")
                                        .executes(context -> doCommand(context.getSource(),"marker",context.getArgument("racename",String.class),"all")))
                                .then(HelpfulLiteralBuilder.literal("checkpoint")
                                        .executes(context -> doCommand(context.getSource(),"marker",context.getArgument("racename",String.class),"checkpoint")))
                                .then(HelpfulLiteralBuilder.literal("finish")
                                        .executes(context -> doCommand(context.getSource(),"marker",context.getArgument("racename",String.class),"finish")))
                                .then(HelpfulLiteralBuilder.literal("start")
                                        .executes(context -> doCommand(context.getSource(),"marker",context.getArgument("racename",String.class),"start")))))
                .then(HelpfulLiteralBuilder.literal("raceset")
                        .withHelpText("Defines a race game location.")
                        .withTooltip("start|finish|checkpoint [checkpointID] [-i]: With argument 'start' or 'finish' defines the start or finish of the game. With argument 'checkpoint' defines a race checkpoint which racing players " +
                                "have to visit in proper order. Without further arguments after 'checkpoint' a new checkpoint is added after the last existing checkpoint. With argument 'checkpointID' the checkpoint with that ID is moved " +
                                "to your location. With optional argument '-i' a new checkpoint is inserted in front of the checkpoint with ID 'checkpointID'.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .then(HelpfulLiteralBuilder.literal("checkpoint")
                                .executes(context -> doCommand(context.getSource(), "raceset", "checkpoint"))
                                .then(HelpfulRequiredArgumentBuilder.argument("checkpointID", integer())
                                        .executes(context -> doCommand(context.getSource(), "raceset", "checkpoint", String.valueOf(context.getArgument("checkpoint", Integer.class))))
                                        .then(HelpfulLiteralBuilder.literal("-i")
                                                .executes(context -> doCommand(context.getSource(), "raceset", "checkpoint", String.valueOf(context.getArgument("checkpointID", Integer.class)), "-i")))))
                        .then(HelpfulLiteralBuilder.literal("finish")
                                .executes(context -> doCommand(context.getSource(), "raceset", "finish")))
                        .then(HelpfulLiteralBuilder.literal("start")
                                .executes(context -> doCommand(context.getSource(), "raceset", "start"))))
                .then(HelpfulLiteralBuilder.literal("resetscore")
                        .withHelpText("Resets Scores.")
                        .withTooltip("Resets the highscore and PBs of the loaded race, for example when replanned or unwanted person.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.STAFF) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .executes(context -> doCommand(context.getSource(),"resetscore", null)))
                .then(HelpfulLiteralBuilder.literal("remove")
                        .withHelpText("Removes a race checkpoint.")
                        .withTooltip("Removes the race checkpoint with [checkpointID]. Without argument attempts to remove a nearby (10 blocks) checkpoint.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .executes(context -> doCommand(context.getSource(), "remove", null))
                        .then(HelpfulRequiredArgumentBuilder.argument("checkpointID",integer())
                                .executes(context -> doCommand(context.getSource(), "remove", String.valueOf(context.getArgument("checkpointID",Integer.class))))))
                .then(HelpfulLiteralBuilder.literal("saverace")
                        .withHelpText("Saves a race to file.")
                        .withTooltip("Saves the race locations with the assigned marker names and a <description> to file <filename>.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                .then(HelpfulRequiredArgumentBuilder.argument("description",greedyString())
                                        .executes(context -> doCommand(context.getSource(),"saverace", context.getArgument("filename",String.class),context.getArgument("description",String.class))))))
                .then(HelpfulLiteralBuilder.literal("savemarker")
                        .withHelpText("Creates and saves a race marker to file.")
                        .withTooltip("Creates and saves a race marker to file <filename>. All non-Air blocks within 10 blocks radius of the player who issues the command are saved to the marker. Use Netherrack for check locations. " +
                                "A racing player needs to move to a check location to be registered at the location. Signs will be labeled for races.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                .executes(context -> doCommand(context.getSource(), "savemarker", context.getArgument("filename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("show")
                        .withHelpText("Shows rankings for a race.")
                        .withTooltip("'start' shows the starter list to all participating and spectating players. 'finish' shows the final ranking of the game. A numeric argument shows the intermediate ranking at the checkpoint" +
                                " with 'checkpointID'.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .then(HelpfulLiteralBuilder.literal("finish")
                                .executes(context -> doCommand(context.getSource(), "show", "finish")))
                        .then(HelpfulLiteralBuilder.literal("start")
                                .executes(context -> doCommand(context.getSource(), "show", "start")))
                        .then(HelpfulRequiredArgumentBuilder.argument("checkpointID",integer())
                                .executes(context -> doCommand(context.getSource(), "show", String.valueOf(context.getArgument("checkpointID", Integer.class))))))
                .then(HelpfulLiteralBuilder.literal("stats")
                        .withHelpText("Shows your PB for the race.")
                        .withTooltip("Gives you infos about your personal best time for the loaded race.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER) && PluginData.isInGame(sender) && PluginData.isCorrectGameType(sender, type))
                        .executes(context -> doCommand(context.getSource(), "stats", null)))
                .then(HelpfulLiteralBuilder.literal("stop")
                        .withHelpText("Stops a race.")
                        .withTooltip("Aborts an already started race. Useful when there is a problem with a checkpoint or to let some other players join the race. Race can be started again.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .executes(context -> doCommand(context.getSource(), "stop", null)))
                .then(HelpfulLiteralBuilder.literal("tpcp")
                        .withHelpText("Teleport to last checkpoint.")
                        .withTooltip("Lets the user teleport to the last checkpoint, but only one time, like a last chance save.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER) && PluginData.isInGame(sender) && PluginData.isCorrectGameType(sender, type))
                        .executes(context -> doCommand(context.getSource(), "tpcp", null)))
                .then(HelpfulLiteralBuilder.literal("tpstart")
                        .withHelpText("TPs Manager to start point.")
                        .withTooltip("Can be used to teleport to a race start directly after loading the race, that it you don´t need to find it. Sets race start also there.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender, type))
                        .executes(context -> doCommand(context.getSource(), "tpstart", null)));
        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        RaceGame racegame;

        switch (command){
            case "loadrace":
                sendNotImplementedYetMessage(sender);
                break;
            case "marker":
                sendNotImplementedYetMessage(sender);
                break;
            case "raceset":
                sendNotImplementedYetMessage(sender);
                break;
            case "resetscore":
                sendNotImplementedYetMessage(sender);
                break;
            case "remove":
                sendNotImplementedYetMessage(sender);
                break;
            case "saverace":
                sendNotImplementedYetMessage(sender);
                break;
            case "savemarker":
                sendNotImplementedYetMessage(sender);
                break;
            case "show":
                sendNotImplementedYetMessage(sender);
                break;
            case "stats":
                sendNotImplementedYetMessage(sender);
                break;
            case "stop":
                sendNotImplementedYetMessage(sender);
                break;
            case "tpcp":
                sendNotImplementedYetMessage(sender);
                break;
            case "tpstart":
                sendNotImplementedYetMessage(sender);
                break;
            default:
                sendNothereMessage(sender);
        }

        return 0;
    }

    private void sendNothereMessage(McmeCommandSender sender){
        PluginData.getMessageUtil().sendErrorMessage(sender,"You shouldn't be here. Try Again!");
    }

    private void sendNotImplementedYetMessage(McmeCommandSender sender){
        PluginData.getMessageUtil().sendErrorMessage(sender,"This is not yet implemented.");
    }
}
