package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GeoGuessrGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */

public class GeoGameSetRounds extends AbstractGameCommand{

    public GeoGameSetRounds(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.GEO_GUESSR;
        setShortDescription("Sets the number of rounds.");
        setUsageDescription("Sets the number of rounds. Default is 5 ");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs, game) && isCorrectGameType((Player)cs,game, GameType.GEO_GUESSR)) {
            GeoGuessrGame geogame = (GeoGuessrGame) game;
            if(!geogame.isStarted()) {
                if (Integer.parseInt(args[0]) > 0) {
                    geogame.setRoundNumber(Integer.parseInt(args[0]));
                    sendRoundsSet(cs, args[0]);
                } else {
                    sendRoundsMustBePositiveErrorMessage(cs);
                }
            }else{
                sendGameStarted(cs);
            }

        }

    }
    private void sendGameStarted(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"The game has already started.");
    }
    private void sendRoundsMustBePositiveErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The number of rounds must be >= 1.");
    }
    private void sendRoundsSet(CommandSender cs, String roundNumber){
        String String_area = "You set the round-number to " + roundNumber + ".";
        PluginData.getMessageUtil().sendInfoMessage(cs,String_area);
    }

}
