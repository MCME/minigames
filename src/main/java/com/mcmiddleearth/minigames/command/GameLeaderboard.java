package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.highscores.gameWinHighscore;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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
            sendLeaderboard((Player)cs,String.valueOf(leaderboard),game);
        }else{
            sendGameNotFound(cs);
        }

    }
    private void sendLeaderboard(Player player, String wins, String game){
        if(game.equalsIgnoreCase("seek")){
            PluginData.getMessageUtil().sendInfoMessage(player, "The leaderboard for seeking:"+wins);
        }else if(game.equalsIgnoreCase("hide")){
            PluginData.getMessageUtil().sendInfoMessage(player, "The leaderboard for hiding:"+wins);
        }else {
            PluginData.getMessageUtil().sendInfoMessage(player, "The leaderboard for the game " + game + ":" + wins);
        }
    }

    private void sendGameNotFound(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Game not found.");
    }
}
