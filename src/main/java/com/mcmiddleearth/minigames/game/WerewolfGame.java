package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class WerewolfGame extends AbstractGame{

    public WerewolfGame(Player manager, String name){
        super(manager,name,GameType.WEREWOLF,new QuizGameScoreboard(name, manager));
    }
}
