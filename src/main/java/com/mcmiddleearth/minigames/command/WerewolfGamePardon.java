package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */
public class WerewolfGamePardon extends AbstractGameCommand{

    public WerewolfGamePardon(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.WEREWOLF;
        setShortDescription("/game pardon playername ");
        setUsageDescription("Lets the game manager pardon a player when he was put up for vote.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)){
            WerewolfGame werewolf = (WerewolfGame) game;
            OfflinePlayer player = game.getPlayer(args[0]);
            werewolf.pardon();
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }
}
