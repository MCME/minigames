package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.CatchGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CatchGameCatcher extends AbstractGameCommand{

    public CatchGameCatcher(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.CATCH;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.CATCH)){
            OfflinePlayer catcher = game.getPlayer(args[0]);
            if(catcher == null){
                sendPlayerNotFoundErrorMessage(cs);
            }else{
                CatchGame catchgame = (CatchGame) game;
                catchgame.setCatcher(catcher);
                sendCatcherSetMessage(cs,catcher);
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendCatcherSetMessage(CommandSender cs, OfflinePlayer seeker) {
        PluginData.getMessageUtil().sendInfoMessage(cs, seeker.getName() +" will be the next catcher.");
        if(PlayerUtil.getOnlinePlayer(seeker)!=null)
            PluginData.getMessageUtil().sendInfoMessage(PlayerUtil.getOnlinePlayer(seeker),
                    "You are assigned to be the next catcher.");
    }
}
