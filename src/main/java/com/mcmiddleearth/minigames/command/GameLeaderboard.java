package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.highscores.gameWinHighscore;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author Jubo
 */

public class GameLeaderboard extends AbstractGameCommand{

    public GameLeaderboard(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription("Leaderboard of game");
        setUsageDescription("/game leaderboard game count. Default count is 10");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        String game = args[0];
        int count = 10;
        if(args.length > 1){
            count = StringUtil.parseInt(args[1]);
        }
        gameWinHighscore gameHighscore = new gameWinHighscore();
        if(gameHighscore.gameExists(game)){
            Map<String, Object> leaderboard = gameHighscore.getLeaderboard(game,count);
            sendLeaderboard((Player)cs,leaderboard,game);
        }else{
            sendGameNotFound(cs);
        }

    }
    private void sendLeaderboard(Player player, Map<String, Object> wins, String game){
        if(game.equalsIgnoreCase("seek")){
            PluginData.getMessageUtil().sendInfoMessage(player, "The leaderboard for seeking:");
        }else if(game.equalsIgnoreCase("hide")){
            PluginData.getMessageUtil().sendInfoMessage(player, "The leaderboard for hiding:");
        }else {
            PluginData.getMessageUtil().sendInfoMessage(player, "The leaderboard for the game " + game + ":");
        }
        Map.Entry<String,Object> top;
        int place = 1;
        int j = wins.size();
        for(int i = 1;i <= j; i++) {
            top = null;
            for (Map.Entry<String, Object> entry : wins.entrySet()) {
                if (top == null || (Integer) entry.getValue() > (Integer) top.getValue()) {
                    top = entry;
                }
            }
            sendSorted(player,String.valueOf(top),place++);
            wins.remove(top.getKey());
        }
    }

    private void sendSorted(Player player, String person, int place){
        player.sendMessage(ChatColor.AQUA +String.valueOf(place)+". "+person);
    }

    private void sendGameNotFound(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Game not found.");
    }
}