package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;

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
                .requires(sender -> (sender instanceof MinigameCommandSender));

        return helpfulLiteralBuilder;
    }

    private int sendToGameChat(McmeCommandSender sender, String message){

        return 0;
    }
}
