package com.mcmiddleearth.minigames.core.game;

import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.minigames.core.scoreboard.WerewolfGameScoreboard;

public class WerewolfGame extends AbstractGame{

    public WerewolfGame(McmeProxyPlayer manager, String name){
        super(manager,name,GameType.WEREWOLF,new WerewolfGameScoreboard());
    }
}
