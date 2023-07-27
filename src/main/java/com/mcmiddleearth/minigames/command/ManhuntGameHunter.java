package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManhuntGameHunter extends AbstractGameCommand{

    public ManhuntGameHunter(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.MANHUNT;
        setShortDescription(" Appoints the hunter for the next round.");
        setUsageDescription(" <player>: Appoints <player> to be one of the next hunter. If you type a number, x random players will be selected.");
    }

    @Override
    protected void execute(CommandSender cs,String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.MANHUNT)){
            ManhuntGame manhunt = (ManhuntGame) game;
            OfflinePlayer hunter = game.getPlayer(args[0]);
            if(hunter == null){
                int number = StringUtil.parseInt(args[0]);
                if(number != 0){
                    manhunt.selectRandomHunter(number);
                }else{
                    sendPlayerNotFoundErrorMessage(cs);
                }
            }else{
                manhunt.setHunter(hunter);
                sendHUnterSetMessage((Player)cs,hunter);
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendHUnterSetMessage(CommandSender cs, OfflinePlayer seeker) {
        PluginData.getMessageUtil().sendInfoMessage(cs, seeker.getName() +" will be one of the next hunter.");
    }
}
