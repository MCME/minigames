package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.WerewolfGameScoreboard;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class WerewolfGame extends AbstractGame{

    public WerewolfGame(ProxiedPlayer manager, String name){
        super(manager,name,GameType.WEREWOLF,new WerewolfGameScoreboard());
    }
}
