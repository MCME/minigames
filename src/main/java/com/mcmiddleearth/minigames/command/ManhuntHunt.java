package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Jubo
 */
public class ManhuntHunt extends AbstractGameCommand{

    public ManhuntHunt(String... permissionNodes){
        super(3,true,permissionNodes);
        cmdGroup = CmdGroup.MANHUNT;
        setShortDescription("Start a manhunt game");
        setUsageDescription("/game hunt radius searchTime hideTime");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.MANHUNT)){
            if(game.countOnlinePlayer() < 1){
                sendNotEnoughPlayerErrorMessage(cs);
            }else{
                ManhuntGame manhuntgame = (ManhuntGame) game;
                if(!manhuntgame.isStarted()) {
                    PluginData.startTimerRunnable((Player)cs);
                    BukkitRunnable waitTask;
                    waitTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            int radius = StringUtil.parseInt(args[0]);
                            int searchTime = StringUtil.parseInt(args[1]);
                            int hideTime = StringUtil.parseInt(args[2]);

                            manhuntgame.setSeekTime(searchTime);
                            manhuntgame.setHideTime(hideTime);
                            manhuntgame.hiding(radius);
                        }
                    };
                    waitTask.runTaskLater(MiniGamesPlugin.getPluginInstance(),(20L *PluginData.getTimer())+1);
                } else {
                    sendNeedToRestartErrorMessage(cs);
                }
            }
        }
    }

    private void sendNotEnoughPlayerErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Not enough players in game. Minimum is two.");
    }

    private void sendNeedToRestartErrorMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"The game is already running. If you want to restart. " +
                "You need to do create a new game.");
    }

}
