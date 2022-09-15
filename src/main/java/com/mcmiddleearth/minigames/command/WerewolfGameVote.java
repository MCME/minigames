package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jubo
 */

public class WerewolfGameVote extends AbstractGameCommand{
    public WerewolfGameVote(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.WEREWOLF;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)){
            WerewolfGame werewolf = (WerewolfGame) game;
            if(args[0].equals("yay")){
                werewolf.vote(true);
                sendVoteMessage(true,(Player)cs);
            }else if(args[0].equals("nay")){
                werewolf.vote(false);
                sendVoteMessage(false,(Player)cs);
            }else {
                OfflinePlayer player = game.getPlayer(args[0]);
                if(player != null){
                    if(isManager((Player)cs,game)){
                        werewolf.putUpVote((Player)player);
                        sendPutUpForVoteMessage(player);
                    }else{
                        werewolf.suggest(cs,(Player)player);
                        sendSuggestionMessage(player,cs);
                    }
                }else{
                    sendPlayerNotFoundErrorMessage(cs);
                }
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendVoteMessage(boolean bool,Player player){
        if(bool){
            PluginData.getMessageUtil().sendBroadcastMessage(player.getName()+" voted yay.");
        } else {
            PluginData.getMessageUtil().sendBroadcastMessage(player.getName()+" voted nay.");
        }
    }

    private void sendPutUpForVoteMessage(OfflinePlayer player){
        PluginData.getMessageUtil().sendBroadcastMessage(player.getName()+" was put up for voting.");
    }

    private void sendSuggestionMessage(OfflinePlayer player,CommandSender cs){
        PluginData.getMessageUtil().sendBroadcastMessage(player.getName()+ " was suggested by "+cs.getName());
    }
}
