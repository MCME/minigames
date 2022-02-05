package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.highscores.gameWinHighscore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 *
 * @author Jubo
 */

public class GameStats extends AbstractGameCommand{

    public GameStats(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription("Gives you your wins");
        setUsageDescription("Gives you your wins, splitted by games and Hide and Seeker");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        gameWinHighscore winHighscore = new gameWinHighscore();
        Player player = (Player) cs;
        Map<String,Integer> wins = winHighscore.getWins(player.getUniqueId());
        sendWins((Player)cs,String.valueOf(wins));
    }

    private void sendWins(Player player, String wins){
        PluginData.getMessageUtil().sendInfoMessage(player, "This are the wins you have: "+wins);
    }
}