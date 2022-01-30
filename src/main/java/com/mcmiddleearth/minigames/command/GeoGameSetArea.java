package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GeoGuessrGame;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrAreas;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */

public class GeoGameSetArea extends AbstractGameCommand{

    public GeoGameSetArea(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.GEO_GUESSR;
        setShortDescription("Sets a certain game area");
        setUsageDescription("sets a certain game area. Default is all.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs, game) && isCorrectGameType((Player)cs,game,GameType.GEO_GUESSR)) {
            GeoGuessrAreas geoarea = new GeoGuessrAreas(args[0]);
            if(geoarea.containsArea(args[0])){
                String area = geoarea.getName();
                GeoGuessrGame geogame = (GeoGuessrGame) game;
                if(!geogame.isStarted()) {
                    if (area != null) {
                        geogame.setArea(args[0]);
                        sendAreaSet(cs, geoarea.getName());
                    } else {
                        sendInvalidArgumentMessage(cs);
                    }
                }else{
                    sendGameStarted(cs);
                }
            }else{
                sendNoArea(cs);
            }
        }
    }
    private void sendGameStarted(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"The game has already started.");
    }
    private void sendInvalidArgumentMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Argument.");
    }
    private void sendNoArea(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This area does not exist.");
    }
    private void sendAreaSet(CommandSender cs, String area){
        String String_area = "You set the area to " + area + ".";
        PluginData.getMessageUtil().sendInfoMessage(cs,String_area);
    }
}
