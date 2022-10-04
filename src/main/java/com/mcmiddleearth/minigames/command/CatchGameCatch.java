package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.CatchGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CatchGameCatch extends AbstractGameCommand{

    public CatchGameCatch(String... permissionNodes){
        super(3,true,permissionNodes);
        cmdGroup = CmdGroup.CATCH;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs,String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.CATCH)){
            if(game.countOnlinePlayer()<1){
                sendNotEnoughPlayerErrorMessage(cs);
            }else{
                CatchGame catchgame = (CatchGame) game;
                int radius = StringUtil.parseInt(args[0]);
                int time = StringUtil.parseInt(args[1]);
                int countdown = StringUtil.parseInt(args[2]);
                catchgame.catching(radius,time,countdown);
            }
        }
    }

    private void sendNotEnoughPlayerErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Not enough players in game. Minimum is two.");
    }
}
