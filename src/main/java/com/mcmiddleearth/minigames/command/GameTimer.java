package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */
public class GameTimer extends AbstractGameCommand{

    public GameTimer(String... permissionNodes){
        super(1,true,permissionNodes);
        setShortDescription("/game timer <seconds>");
        setUsageDescription(" Sets a timer before starting the game");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = PluginData.getGame((Player)cs);
        if(game!=null && isManager((Player)cs,game)){
            Integer time = StringUtil.parseInt(args[0]);
            PluginData.setTime(time);
            sendTimerSetMessage(cs,time);
        }
    }

    private void sendTimerSetMessage(CommandSender cs, Integer time) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "The timer was set to "+time+" seconds.");
    }
}
