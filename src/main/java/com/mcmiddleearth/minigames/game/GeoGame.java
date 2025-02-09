package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.GeoGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class GeoGame extends AbstractGame{

    public GeoGame(Player manager, String name){
        super(manager,name,GameType.GEO_GUESSR,new GeoGameScoreboard());
    }
}
