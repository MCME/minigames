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
        setShortDescription("Sets a certain game area");
        setUsageDescription("sets a certain game area. Default is all.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs, game) && isCorrectGameType((Player)cs,game,GameType.GEO_GUESSR)) {
            GeoGuessrAreas area = GeoGuessrAreas.getArea(args[0]);
            GeoGuessrGame geogame = (GeoGuessrGame) game;
            if(!geogame.isStarted()) {
                if (area != null) {
                    geogame.setArea(area);
                    sendAreaSet(cs, area);
                } else {
                    sendInvalidArgumentMessage(cs);
                }
            }else{
                sendGameStarted(cs);
            }
        }
    }
    private void sendGameStarted(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"The game has already started.");
    }
    private void sendInvalidArgumentMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Argument.");
    }
    private void sendAreaSet(CommandSender cs, GeoGuessrAreas area){
        String String_area = "You set the area to " + area.getName() + ".";
        PluginData.getMessageUtil().sendInfoMessage(cs,String_area);
    }
}
