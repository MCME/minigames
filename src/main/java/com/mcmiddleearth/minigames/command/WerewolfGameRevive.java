package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */
public class WerewolfGameRevive extends AbstractGameCommand{

    public WerewolfGameRevive(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.WEREWOLF;
        setShortDescription("/game revive playername");
        setUsageDescription("Adds a player again after he had a disconnect.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)){
            WerewolfGame werewolf = (WerewolfGame) game;
            OfflinePlayer player = Bukkit.getPlayer(args[0]);
            if(player == null){
                sendPlayerNotFoundErrorMessage(cs);
            }else if(player.isOnline()){
                werewolf.Revive((Player)player);
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }
}
