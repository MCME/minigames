package com.mcmiddleearth.minigames.core.game;

import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.minigames.core.scoreboard.ManhuntGameScoreboard;

public class ManhuntGame extends AbstractGame{

    public ManhuntGame(McmeProxyPlayer manager, String name){
        super(manager,name,GameType.MANHUNT,new ManhuntGameScoreboard());
    }
}
