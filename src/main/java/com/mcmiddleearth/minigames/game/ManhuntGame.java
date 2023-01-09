package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.ManhuntGameScoreboard;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ManhuntGame extends AbstractGame{

    public ManhuntGame(ProxiedPlayer manager, String name){
        super(manager,name,GameType.MANHUNT,new ManhuntGameScoreboard());
    }
}
