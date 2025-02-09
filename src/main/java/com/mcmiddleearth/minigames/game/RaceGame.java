package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.RaceGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class RaceGame extends AbstractGame{

    public RaceGame(Player manager, String name){
        super(manager,name,GameType.RACE,new RaceGameScoreboard());
    }
}
