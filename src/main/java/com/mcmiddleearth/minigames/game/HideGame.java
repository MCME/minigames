package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.HideGameScoreboard;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HideGame extends AbstractGame{

    public HideGame(ProxiedPlayer manager, String name){
        super(manager,name,GameType.HIDE_AND_SEEK,new HideGameScoreboard());
    }
}
