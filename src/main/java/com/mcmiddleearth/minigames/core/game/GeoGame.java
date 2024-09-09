package com.mcmiddleearth.minigames.core.game;

import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.minigames.core.scoreboard.GeoGameScoreboard;

public class GeoGame extends AbstractGame{

    public GeoGame(McmeProxyPlayer manager, String name){
        super(manager,name,GameType.GEO_GUESSR,new GeoGameScoreboard());
    }
}
