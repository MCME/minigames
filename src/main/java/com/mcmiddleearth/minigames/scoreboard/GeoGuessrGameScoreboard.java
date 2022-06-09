package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jubo
 */

public class GeoGuessrGameScoreboard extends GameScoreboard{

    private final Objective geoObjective, timerObjective;

    private final Score answerTimeScore, unfinishedScore;

    private int roundCount = 5;

    private int currentRound = 0;

    private final List<String> players = new ArrayList<>();

    private BukkitRunnable timerTask;

    public GeoGuessrGameScoreboard() {
        super("GeoGuessr");
        geoObjective = scoreboard.registerNewObjective("geo", "dummy");
        timerObjective = scoreboard.registerNewObjective("timer", "dummy");
        setRoundDisplay();
        timerObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        answerTimeScore = timerObjective.getScore(ChatColor.YELLOW+" time remaining: ");
        answerTimeScore.setScore(0);
        unfinishedScore = timerObjective.getScore(ChatColor.RED+" players thinking: ");
        unfinishedScore.setScore(0);
    }

    public void startRound(int time, int players, BossBar bar){
        answerTimeScore.setScore(time);
        unfinishedScore.setScore(players);
        setRoundDisplay();
        bar.setTitle(ChatColor.YELLOW+"GeoGuessr: Round "+(currentRound+1));
        double progress = 1.0 / time;
        if(timerTask!=null){
            timerTask.cancel();
        }
        timerObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                answerTimeScore.setScore(answerTimeScore.getScore()-1);
                if(answerTimeScore.getScore()<1) {
                    geoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    bar.setProgress(1.0);
                    cancel();
                }else{
                    bar.setProgress(bar.getProgress()-progress);
                }
            }};
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }

    public void addPlayer(String player){
        if(players.isEmpty()){
            geoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        Score score = geoObjective.getScore(player);
        players.add(player);
        score.setScore(0);
    }

    public void score(String player){
        Score score = geoObjective.getScore(player);
        score.setScore(score.getScore()+10);
    }

    public void firstScore(String player){
        Score score = geoObjective.getScore(player);
        score.setScore(score.getScore()+12);
    }

    public int getScore(String playerName) {
        return geoObjective.getScore(playerName).getScore();
    }

    private void setRoundDisplay(){
        geoObjective.setDisplayName("Round "+currentRound+" / " + roundCount);
        timerObjective.setDisplayName("Round "+currentRound+" / "+ roundCount);
    }

    public void addRound() {
        currentRound++;
        setRoundDisplay();
    }

    public void setRoundCount(int roundCount){
        this.roundCount = roundCount;
        setRoundDisplay();
    }

    public void stopRound(){
        if(timerTask!=null){
            timerTask.cancel();
        }
        geoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    public void playerFinished(){
        unfinishedScore.setScore(unfinishedScore.getScore()-1);
    }

    public void restart(){
        currentRound = 0;
        for(String name: players){
            geoObjective.getScore(name).setScore(0);
        }
        setRoundDisplay();
    }
}
