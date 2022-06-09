package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManhuntSeeker extends AbstractGameCommand{

    public ManhuntSeeker(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.MANHUNT;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.MANHUNT)){
            OfflinePlayer seeker = game.getPlayer(args[0]);
            if(seeker == null){
                sendPlayerNotFoundErrorMessage(cs);
            }else{
                ManhuntGame manhuntgame = (ManhuntGame) game;
                manhuntgame.setSeeker(seeker);
                sendSeekerSetMessage(cs,seeker);
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendSeekerSetMessage(CommandSender cs, OfflinePlayer seeker) {
        PluginData.getMessageUtil().sendInfoMessage(cs, seeker.getName() +" will be the next seeker.");
        if(PlayerUtil.getOnlinePlayer(seeker)!=null)
            PluginData.getMessageUtil().sendInfoMessage(PlayerUtil.getOnlinePlayer(seeker),
                    "You are assigned to be the next seeker.");
    }
}
