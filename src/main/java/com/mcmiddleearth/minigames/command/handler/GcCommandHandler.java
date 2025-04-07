package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.command.handler.AbstractCommandHandler;
import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.spigot.util.Permission;
import com.mcmiddleearth.minigames.spigot.util.PluginData;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

/**
 *
 * @author Jubo
 */
public class GcCommandHandler extends AbstractCommandHandler {

    public GcCommandHandler(String name){
        super(name);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder) {
        helpfulLiteralBuilder
                .requires(sender -> (sender instanceof MinigameCommandSender))
                .requires(sender -> PluginData.isInGame(sender) && PluginData.hasPermission(sender, Permission.USER))
                .then(HelpfulRequiredArgumentBuilder.argument("text",greedyString())
                        .executes(context -> sendToGameChat(context.getSource(), context.getArgument("text",String.class))));
        return helpfulLiteralBuilder;
    }
    public static BrigadierCommand createBrigadierCommmand(final ProxyServer proxy){
        LiteralCommandNode<CommandSource> gcNode = BrigadierCommand.literalArgumentBuilder("test")
                .requires(sender -> (sender instanceof MinigameCommandSender))
                .requires(sender -> PluginData.isInGame((McmeCommandSender) sender) && PluginData.hasPermission((McmeCommandSender) sender, Permission.USER))
                .then(BrigadierCommand.requiredArgumentBuilder("text",greedyString())
                        .executes(context -> sendToGameChat((McmeCommandSender) context.getSource(), context.getArgument("text",String.class)))).build();

        return new BrigadierCommand(gcNode);
    }

    private static int sendToGameChat(McmeCommandSender sender, String message){
        AbstractGame game = PluginData.getGame(sender);
        Player player = ((MinigameCommandSender) sender).getCommandSender();
        if(game != null){
            game.gameChat(player,message);
        }
        return 0;
    }
}
