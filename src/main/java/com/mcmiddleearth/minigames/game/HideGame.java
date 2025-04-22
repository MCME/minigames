package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class HideGame extends AbstractGame{

    public HideGame(Player manager, String name){
        super(manager,name,GameType.HIDE_AND_SEEK,new QuizGameScoreboard(name, manager));
    }
}
