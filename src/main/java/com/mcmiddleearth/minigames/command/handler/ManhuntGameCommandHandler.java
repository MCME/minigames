package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import com.mcmiddleearth.minigames.util.PluginData;

public class ManhuntGameCommandHandler {

    public ManhuntGameCommandHandler(){}

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){

        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        ManhuntGame manhuntGame;

        switch (command){

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
