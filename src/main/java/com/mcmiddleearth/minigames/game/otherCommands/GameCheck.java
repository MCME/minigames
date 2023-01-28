package com.mcmiddleearth.minigames.game.otherCommands;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;

/**
 * @author Jubo
 */
public class GameCheck {

    public static void sendInfos(McmeCommandSender sender){
        if(!PluginData.gameRunning()){
            PluginData.getMessageUtil().sendInfoMessage(sender,"There is currently no game running.");
        }else {
            PluginData.getMessageUtil().sendInfoMessage(sender,"Running games (click to join):");
            for(String game : PluginData.getGames()){
                PluginData.getMessageUtil().sendClickableInfoMessage(((MinigameCommandSender)sender).getCommandSender(), Style.HIGHLIGHT+PluginData.getGame(game).getManager().getName()
                        +Style.INFO+": Do "+ Style.STRESSED+"/tour join "+game+Style.INFO+" to join this tour.","/tour join "+game);
            }
        }
    }
}
