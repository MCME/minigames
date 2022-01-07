package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HaSGameGlow extends AbstractGameCommand{
    public HaSGameGlow(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.HIDE_AND_SEEK;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if (game != null && isManager((Player) cs, game) && isCorrectGameType((Player) cs, game, GameType.HIDE_AND_SEEK)) {
            if(args[0].equalsIgnoreCase("all")){
                HideAndSeekGame hidegame = (HideAndSeekGame) game;
                hidegame.setGlow((Player) cs);
            }else {
                sendSubcommandNotFoundErrorMessage(cs);
            }
        }
    }
    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Subcommand not found.");
    }
}
