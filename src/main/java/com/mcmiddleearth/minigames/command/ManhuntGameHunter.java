package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManhuntGameHunter extends AbstractGameCommand{

    public ManhuntGameHunter(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.MANHUNT;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs,String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.MANHUNT)){
            OfflinePlayer hunter = game.getPlayer(args[0]);
            if(hunter == null){
                // TODO:
                //  zufällige zuordnung von args[0] huntern
            }else{
                ManhuntGame manhunt = (ManhuntGame) game;
                manhunt.setSeeker(hunter);
                sendHUnterSetMessage((Player)cs,hunter);
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendHUnterSetMessage(CommandSender cs, OfflinePlayer seeker) {
        PluginData.getMessageUtil().sendInfoMessage(cs, seeker.getName() +" will be a next seeker.");
    }
}
