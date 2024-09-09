package com.mcmiddleearth.minigames.core.game;

import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.minigames.core.scoreboard.RaceGameScoreboard;

public class RaceGame extends AbstractGame{

    public RaceGame(McmeProxyPlayer manager, String name){
        super(manager,name,GameType.RACE,new RaceGameScoreboard());
    }
}
