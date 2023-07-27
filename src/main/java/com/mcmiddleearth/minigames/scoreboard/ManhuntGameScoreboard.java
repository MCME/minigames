package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 *
 * @author Jubo
 */
public class ManhuntGameScoreboard extends GameScoreboard{

    private final Objective hidingObjective;
    private final Objective seekingObjective;

    private final Score hidingTimeScore;
    private final Score seekingTimeScore;
    private final Score hiddenPlayerScore;
    private final Score locatedPlayerScore;

    private static String title = "Game running";
    private static String playerCountTitle = "Game starting";

    private BukkitRunnable timerTask;

    public ManhuntGameScoreboard(){
        super(playerCountTitle);
        hidingObjective = scoreboard.registerNewObjective("HidingTime", "dummy");
        hidingObjective.setDisplayName(title);
        hidingTimeScore = hidingObjective.getScore(ChatColor.YELLOW+"hiding time remaining: ");

        seekingObjective = scoreboard.registerNewObjective("SeekingTime", "dummy");
        seekingObjective.setDisplayName(title);
        seekingTimeScore = seekingObjective.getScore(ChatColor.YELLOW+"seeking time remaining: ");
        hiddenPlayerScore = seekingObjective.getScore(ChatColor.RED+"hidden Players: ");
        locatedPlayerScore = seekingObjective.getScore(ChatColor.GREEN+"located Players: ");
    }

    public void startHiding(int hidingTime,BossBar bar) {
        hidingObjective.setDisplayName(title);
        seekingObjective.setDisplayName(title);
        hidingTimeScore.setScore(hidingTime);
        hidingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        double progress = 1.0 / hidingTime;
        if(timerTask!=null) {
            timerTask.cancel();
        }
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                hidingTimeScore.setScore(hidingTimeScore.getScore()-1);
                if(hidingTimeScore.getScore()<1) {
                    bar.setProgress(1.0);
                    cancel();
                }else{
                    bar.setProgress(bar.getProgress()-progress);
                }
            }};
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }

    public void startSeeking(int seekingTime,BossBar bar, int seekerCount) {
        seekingTimeScore.setScore(seekingTime);
        hiddenPlayerScore.setScore(getPlayerCount()-seekerCount);
        locatedPlayerScore.setScore(0);
        seekingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        double progress = 1.0 / seekingTime;
        if(timerTask!=null) {
            timerTask.cancel();
        }
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                seekingTimeScore.setScore(seekingTimeScore.getScore()-1);
                if(seekingTimeScore.getScore()<1) {
                    bar.setProgress(1.0);
                    cancel();
                }else{
                    bar.setProgress(bar.getProgress()-progress);
                }
            }};
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }

    public void stop() {
        getPlayerCountObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
        if(timerTask!=null) {
            timerTask.cancel();
        }
        timerTask = null;
        title = "Game running";
        playerCountTitle = "Game starting";
        hidingObjective.setDisplayName("Test");
        seekingObjective.setDisplayName("Test2");
        getPlayerCountObjective().setDisplayName("Game finished");
    }

    public void locatePlayer() {
        hiddenPlayerScore.setScore(hiddenPlayerScore.getScore()-1);
        locatedPlayerScore.setScore(locatedPlayerScore.getScore()+1);
    }
}
