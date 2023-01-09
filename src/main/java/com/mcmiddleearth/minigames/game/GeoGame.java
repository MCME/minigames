package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.GeoGameScoreboard;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GeoGame extends AbstractGame{

    public GeoGame(ProxiedPlayer manager, String name){
        super(manager,name,GameType.GEO_GUESSR,new GeoGameScoreboard());
    }
}
