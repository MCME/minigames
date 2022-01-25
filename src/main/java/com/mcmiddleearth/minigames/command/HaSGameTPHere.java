package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */

public class HaSGameTPHere extends AbstractGameCommand {

    public HaSGameTPHere(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.HIDE_AND_SEEK;
        setShortDescription("Teleports player to user");
        setUsageDescription("/game tphere <player> . Will tp the player to the manager, if he gets stuck");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if (game != null && isManager((Player) cs, game) && isCorrectGameType((Player) cs, game, GameType.HIDE_AND_SEEK)) {
            HideAndSeekGame hidegame = (HideAndSeekGame) game;
            OfflinePlayer player = game.getPlayer(args[0]);
            if (player == null) {
                sendPlayerNotFoundErrorMessage(cs);
            } else {
                if(hidegame.teleportToManager((Player) cs, player)){
                    sendTPdPlayer(cs, player);
                }
            }
        }
    }

    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendTPdPlayer(CommandSender cs, OfflinePlayer player) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You tp´d " + player.getName() + " to you.");
    }
}
