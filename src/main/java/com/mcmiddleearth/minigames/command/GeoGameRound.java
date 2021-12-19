package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GeoGuessrGame;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */

public class GeoGameRound extends AbstractGameCommand{

    public GeoGameRound(String... permissionNodes){
        super(0,true,permissionNodes);
        cmdGroup = CmdGroup.GEO_GUESSR;
        setShortDescription(": TPs to next warp.");
        setUsageDescription("/game send (radius) (time) . Teleports all game participants to the next Warp. If only one parameter is given it sets the time to 30 seconds.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) && isCorrectGameType((Player) cs,game, GameType.GEO_GUESSR)){
            GeoGuessrGame geogame = (GeoGuessrGame) game;
            if(geogame.isPlayerInRound()){
                sendPlayerinRound(cs);
                return;
            }
            if(game.countOnlinePlayer() < 2){   // needs to be switched to 2 after testing
                sendNotEnoughPlayerErrorMessage(cs);
            }
            else{

                int radius = StringUtil.parseInt(args[0]);
                geogame.setGuessRadius(radius);
                if(args.length>1){
                    int guessTime = StringUtil.parseInt(args[1]);
                    geogame.setGuessTime(guessTime);
                }
                geogame.sendRound();

            }

        }
    }
    private void sendNotEnoughPlayerErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Not enough players in game. Minimum is two.");
    }
    private void sendPlayerinRound(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Wait until all players finished this round.");
    }
}
