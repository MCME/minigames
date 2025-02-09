package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.WerewolfGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class WerewolfGame extends AbstractGame{

    public WerewolfGame(Player manager, String name){
        super(manager,name,GameType.WEREWOLF,new WerewolfGameScoreboard());
    }
}
