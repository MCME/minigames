package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class ManhuntGame extends AbstractGame{

    public ManhuntGame(Player manager, String name){
        super(manager,name,GameType.MANHUNT,new QuizGameScoreboard(name, manager));
    }
}
