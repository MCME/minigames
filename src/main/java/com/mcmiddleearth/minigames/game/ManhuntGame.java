package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.ManhuntGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class ManhuntGame extends AbstractGame{

    public ManhuntGame(Player manager, String name){
        super(manager,name,GameType.MANHUNT,new ManhuntGameScoreboard());
    }
}
