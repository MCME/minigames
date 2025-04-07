package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.command.argument.CommandPlayerArgument;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideGame;
import com.mcmiddleearth.minigames.spigot.util.Permission;
import com.mcmiddleearth.minigames.spigot.util.PluginData;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class HideGameCommandHandler {

    GameType type = GameType.HIDE_AND_SEEK;

    public HideGameCommandHandler(){}

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){
        helpfulLiteralBuilder
                .then(HelpfulLiteralBuilder.literal("hide")
                        .withHelpText("Starts a round of Hide and Seek.")
                        .withTooltip("The number <radius> determines the size of a sphere which cages the players. When no [seektime] in seconds is given seek time is 300 sec by default. Without a given [hidetime] in seconds, " +
                                "time for hiding is 60 sec.The number <radius> determines the size of a sphere which cages the players. When no [seektime] in seconds is given seek time is 300 sec by default. " +
                                "Without a given [hidetime] in seconds, time for hiding is 60 sec.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("radius",integer())
                                .then(HelpfulRequiredArgumentBuilder.argument("seektime",integer())
                                        .then(HelpfulRequiredArgumentBuilder.argument("hidetime",integer())
                                                .executes(context -> doCommand(context.getSource(), "hide",String.valueOf(context.getArgument("radius",Integer.class)),
                                                        String.valueOf(context.getArgument("seektime",Integer.class)),String.valueOf(context.getArgument("hidetime",Integer.class))))))))
                .then(HelpfulLiteralBuilder.literal("radius")
                        .withHelpText("Changes the radius")
                        .withTooltip("can change the radius in Hide and Seek during the game")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("radius",integer())
                                .executes(context -> doCommand(context.getSource(), "radius",String.valueOf(context.getArgument("radius",Integer.class))))))
                .then(HelpfulLiteralBuilder.literal("seeker")
                        .withHelpText("Appoints the seeker for the next round.")
                        .withTooltip("Appoints <player> to be next seeker. Without using this command seeker will be randomly chosen from all players.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("player",new CommandPlayerArgument())
                                .executes(context -> doCommand(context.getSource(), "seeker",context.getArgument("player",String.class)))))
                .then(HelpfulLiteralBuilder.literal("tphere")
                        .withHelpText("Teleports player to user.")
                        .withTooltip("Will tp the player to the manager, if he gets stuck.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("player",new CommandPlayerArgument())
                                .executes(context -> doCommand(context.getSource(), "tphere", context.getArgument("player",String.class)))))
                .then(HelpfulLiteralBuilder.literal("unstuck")
                        .withHelpText("Teleports you back to the game warp.")
                        .withTooltip("Teleports you back to the game warp after not moving for 10sec as a hider and 5sec as a seeker.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .executes(context -> doCommand(context.getSource(), "unstuck", null)));
        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        HideGame hideGame;

        switch (command){
            case "hide":
                sendNotImplementedYetMessage(sender);
                break;
            case "radius":
                sendNotImplementedYetMessage(sender);
                break;
            case "seeker":
                sendNotImplementedYetMessage(sender);
                break;
            case "tphere":
                sendNotImplementedYetMessage(sender);
                break;
            case "unstuck":
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
