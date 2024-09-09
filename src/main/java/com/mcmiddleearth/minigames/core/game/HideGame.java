package com.mcmiddleearth.minigames.core.game;

import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.minigames.core.scoreboard.HideGameScoreboard;

public class HideGame extends AbstractGame {

    public HideGame(McmeProxyPlayer manager, String name){
        super(manager,name,GameType.HIDE_AND_SEEK,new HideGameScoreboard());
    }
}
