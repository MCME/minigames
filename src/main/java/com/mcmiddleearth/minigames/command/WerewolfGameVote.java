package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */
public class WerewolfGameVote extends AbstractGameCommand{
    public WerewolfGameVote(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.WEREWOLF;
        setShortDescription("/game vote playername/yay/nay");
        setUsageDescription("As Manager: /game vote playername to nominate; As Player: /game vote playername to suggest and /game vote yay/nay");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)){
            WerewolfGame werewolf = (WerewolfGame) game;
            if(args[0].equals("yay")){
                werewolf.vote(cs,true);
            }else if(args[0].equals("nay")){
                werewolf.vote(cs,false);
            }else {
                OfflinePlayer player = game.getPlayer(args[0]);
                if(player != null){
                    werewolf.suggest(cs,(Player)player);
                }else{
                    sendPlayerNotFoundErrorMessage(cs);
                }
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

}
