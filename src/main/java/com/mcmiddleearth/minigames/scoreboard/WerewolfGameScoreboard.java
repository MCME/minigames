package com.mcmiddleearth.minigames.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;

public class WerewolfGameScoreboard extends GameScoreboard{

    private final Objective voteObjective;

    private final Objective suggestObjective;

    private final List<String> players = new ArrayList<>();

    private final Score yayScore;

    private final Score nayScore;


    private final static String voteTitle = "To be eliminated: ";

    public WerewolfGameScoreboard(){
        super("Game starting");
        voteObjective = scoreboard.registerNewObjective("Suggest","dummy");
        //voteObjective.setDisplayName(voteTitle+"?");
        yayScore = voteObjective.getScore(ChatColor.RED+"yay: ");
        nayScore = voteObjective.getScore(ChatColor.GREEN+"nay: ");

        suggestObjective = scoreboard.registerNewObjective("Game starting","dummy");
        suggestObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    public void start(){
        suggestObjective.setDisplayName("Game running");
        suggestObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        //suggestObjective.setDisplayName("");
    }

    public void addPlayer(String player){
        if(players.isEmpty()){
            suggestObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        Score score = suggestObjective.getScore(player);
        players.add(player);
        score.setScore(0);
    }

    public void suggest(String player){
        Score score = voteObjective.getScore(player);
        score.setScore(score.getScore()+1);
    }

    public void vote(boolean bool){
        if(bool){
            yayScore.setScore(yayScore.getScore()+1);
        }else{
            nayScore.setScore(nayScore.getScore()+1);
        }

    }

    public void putUpForVote(String player){
        //scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        voteObjective.setDisplayName(voteTitle+player);
        yayScore.setScore(0);
        nayScore.setScore(0);
        voteObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void reset(String player){
        //players.remove(player);
        for(String p : players){
            Score score = suggestObjective.getScore(p);
            score.setScore(0);
        }
        suggestObjective.setDisplayName(voteTitle+"?");
        suggestObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
}
