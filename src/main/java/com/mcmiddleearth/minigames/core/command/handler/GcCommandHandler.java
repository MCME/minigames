package com.mcmiddleearth.minigames.core.command.handler;

import com.mcmiddleearth.base.core.command.McmeCommandSender;
import com.mcmiddleearth.base.core.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.base.core.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.base.core.command.handler.AbstractCommandHandler;
import com.mcmiddleearth.minigames.bungee.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.core.game.AbstractGame;
import com.mcmiddleearth.minigames.core.util.Permission;
import com.mcmiddleearth.minigames.core.util.PluginData;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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

    private int sendToGameChat(McmeCommandSender sender, String message){
        AbstractGame game = PluginData.getGame(sender);
        ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
        if(game != null){
            game.gameChat(player,message);
        }
        return 0;
    }
}
