package com.mcmiddleearth.minigames.core.command.handler;

import com.mcmiddleearth.base.core.command.McmeCommandSender;
import com.mcmiddleearth.base.core.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.base.core.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.core.command.argument.CommandPlayerArgument;
import com.mcmiddleearth.minigames.core.command.argument.CommandWerewolfRolesArgument;
import com.mcmiddleearth.minigames.core.game.GameType;
import com.mcmiddleearth.minigames.core.game.WerewolfGame;
import com.mcmiddleearth.minigames.core.util.Permission;
import com.mcmiddleearth.minigames.core.util.PluginData;

public class WerewolfGameCommandHandler {

    GameType type = GameType.WEREWOLF;

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){
        helpfulLiteralBuilder
                .then(HelpfulLiteralBuilder.literal("config")
                        .withHelpText("Lets the game manager configure the roles for the werewolf game.")
                        .withTooltip("Lets the game manager configure the roles for the werewolf game.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(), "config", null)))
                .then(HelpfulLiteralBuilder.literal("eliminate")
                        .withHelpText("Elimintas a player.")
                        .withTooltip("Elimintas a player.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("player",new CommandPlayerArgument())
                                .executes(context -> doCommand(context.getSource(), "eliminate", context.getArgument("player",String.class)))))
                .then(HelpfulLiteralBuilder.literal("list")
                        .withHelpText("List of players in werewolf.")
                        .withTooltip("Gives a list of eliminated and alive players in werewolf.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER) && PluginData.isCorrectGameType(sender,type) && PluginData.isInGame(sender))
                        .executes(context -> doCommand(context.getSource(), "list", null)))
                .then(HelpfulLiteralBuilder.literal("pardon")
                        .withHelpText("Lets the game manager pardon a player when he was put up for vote.")
                        .withTooltip("Lets the game manager pardon a player when he was put up for vote.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(), "pardon", null)))
                .then(HelpfulLiteralBuilder.literal("revive")
                        .withHelpText("Adds a player again after he had a disconnect.")
                        .withTooltip("Adds a player again after he had a disconnect.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("player",new CommandPlayerArgument())
                                .executes(context -> doCommand(context.getSource(), "revive", context.getArgument("player", String.class)))))
                .then(HelpfulLiteralBuilder.literal("roleinfo")
                        .withHelpText("Gives you the rolebook for the requested role.")
                        .withTooltip("Gives you the rolebook for the requested role.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER) && PluginData.isCorrectGameType(sender,type) && PluginData.isInGame(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("role", new CommandWerewolfRolesArgument())
                                .executes(context -> doCommand(context.getSource(),"roleinfo", context.getArgument("role",String.class)))))
                .then(HelpfulLiteralBuilder.literal("vote")
                        .withHelpText("Lets you vote in werewolf.")
                        .withTooltip("As Manager: /game vote playername to nominate; As Player: /game vote playername to suggest and /game vote yay/nay.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER) && PluginData.isCorrectGameType(sender,type) && PluginData.isInGame(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("player", new CommandPlayerArgument())
                                .executes(context -> doCommand(context.getSource(), "vote",  context.getArgument("player",String.class))))
                        .then(HelpfulLiteralBuilder.literal("yay")
                                .executes(context -> doCommand(context.getSource(), "vote", "yay")))
                        .then(HelpfulLiteralBuilder.literal("nay")
                                .executes(context -> doCommand(context.getSource(), "vote", "nay"))));
        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        WerewolfGame werewolfGame;

        switch (command){
            case "config":
                sendNotImplementedYetMessage(sender);
                break;
            case "eliminate":
                sendNotImplementedYetMessage(sender);
                break;
            case "list":
                sendNotImplementedYetMessage(sender);
                break;
            case "pardon":
                sendNotImplementedYetMessage(sender);
                break;
            case "revive":
                sendNotImplementedYetMessage(sender);
                break;
            case "roleinfo":
                sendNotImplementedYetMessage(sender);
                break;
            case "vote":
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
