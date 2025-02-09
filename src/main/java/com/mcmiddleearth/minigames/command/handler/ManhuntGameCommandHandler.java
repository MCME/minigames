package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.command.argument.CommandPlayerArgument;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import com.mcmiddleearth.minigames.util.Permission;
import com.mcmiddleearth.minigames.util.PluginData;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class ManhuntGameCommandHandler {

    GameType type = GameType.MANHUNT;

    public ManhuntGameCommandHandler(){}

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){
        helpfulLiteralBuilder
                .then(HelpfulLiteralBuilder.literal("hunt")
                        .withHelpText("Start a manhunt game.")
                        .withTooltip("Start a manhunt game.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("radius",integer())
                                .then(HelpfulRequiredArgumentBuilder.argument("hunttime",integer())
                                        .then(HelpfulRequiredArgumentBuilder.argument("hidetime",integer())
                                                .executes(context -> doCommand(context.getSource(), "hide",String.valueOf(context.getArgument("radius",Integer.class)),
                                                        String.valueOf(context.getArgument("hunttime",Integer.class)),String.valueOf(context.getArgument("hidetime",Integer.class))))))))
                .then(HelpfulLiteralBuilder.literal("hunter")
                        .withHelpText("Appoints the hunter for the next round.")
                        .withTooltip("Appoints <player> to be one of the next hunter. If you type a number, x random players will be selected.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isManager(sender) && PluginData.isCorrectGameType(sender,type))
                        .then(HelpfulRequiredArgumentBuilder.argument("player",new CommandPlayerArgument())
                                .executes(context -> doCommand(context.getSource(), "hunter", context.getArgument("player",String.class))))
                        .then(HelpfulRequiredArgumentBuilder.argument("number",integer())
                                .executes(context -> doCommand(context.getSource(), "hunter", String.valueOf(context.getArgument("number",Integer.class))))))
                .then(HelpfulLiteralBuilder.literal("hunterlist")
                        .withHelpText("Shows all hunters.")
                        .withTooltip("an give everyone in the game a list of all hunters")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER) && PluginData.isInGame(sender) && PluginData.isCorrectGameType(sender,type))
                        .executes(context -> doCommand(context.getSource(), "hunterlist", null)));
        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        ManhuntGame manhuntGame;

        switch (command){
            case "hunt":
                sendNotImplementedYetMessage(sender);
                break;
            case "hunter":
                sendNotImplementedYetMessage(sender);
                break;
            case "hunterlist":
                sendNotImplementedYetMessage(sender);
                break;
            default:
                sendNothereMessage(sender);
        }

        return 0;
    }

    private void sendNothereMessage(McmeCommandSender sender){
        //PluginData.getMessageUtil().sendErrorMessage(sender,"You shouldn't be here. Try Again!");
    }

    private void sendNotImplementedYetMessage(McmeCommandSender sender){
        //PluginData.getMessageUtil().sendErrorMessage(sender,"This is not yet implemented.");
    }
}
