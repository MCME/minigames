package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.RaceGameScoreboard;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RaceGame extends AbstractGame{

    public RaceGame(ProxiedPlayer manager, String name){
        super(manager,name,GameType.RACE,new RaceGameScoreboard());
    }
}
