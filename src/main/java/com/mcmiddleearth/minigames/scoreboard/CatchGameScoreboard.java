package com.mcmiddleearth.minigames.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class CatchGameScoreboard extends GameScoreboard{

    private final static String playerCountTitle = "Next Catcher: ";
    private final static String title = "Catcher: ";

    private BukkitRunnable timerTask;

    private final Objective catchObjective;

    private final Score catchTimeScore;

    public CatchGameScoreboard(){
        super(playerCountTitle+"?");
        catchObjective = scoreboard.registerNewObjective("CatchTime","dummy");
        catchObjective.setDisplayName(title+"?");
        catchTimeScore = catchObjective.getScore(ChatColor.YELLOW+"time remaining: ");
    }

    public void startCatch(int time){

    }
}
