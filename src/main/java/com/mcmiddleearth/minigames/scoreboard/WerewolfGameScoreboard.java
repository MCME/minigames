package com.mcmiddleearth.minigames.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;

public class WerewolfGameScoreboard extends GameScoreboard{
    private final Objective objective;

    private final Objective objective2;

    private final List<String> players = new ArrayList<>();

    public WerewolfGameScoreboard(){
        super("Game starting");
        objective = scoreboard.registerNewObjective("Start","dummy");
        objective2 = scoreboard.registerNewObjective("","dummy");
    }
    public void start(){
        objective.setDisplayName("Game running");
        objective2.setDisplayName("");
    }

    public void addPlayer(String player){
        if(players.isEmpty()){
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        Score score = objective.getScore(player);
        players.add(player);
        score.setScore(0);
    }

    public void suggest(String player){
        Score score = objective.getScore(player);
        score.setScore(score.getScore()+1);
    }

    public void vote(boolean bool){


    }

    public void putUpForVote(String player){
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        objective.getScore("yay: ");
        objective2.getScore("nay: ");
    }

    public void reset(){
        for(String player : players){
            Score score = objective.getScore(player);
            score.setScore(0);
        }
    }
}
