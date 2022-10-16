package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */

public class GameHiddenList extends AbstractGameCommand{

    public GameHiddenList(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription("Shows all hidden Players.");
        setUsageDescription("/game hiddenlist. Can give everyone in the game a list of all hidden players");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && game.isInGame((Player) cs)) {
            if(game instanceof HideAndSeekGame) {
                HideAndSeekGame hidegame = (HideAndSeekGame) game;
                hidegame.sendHiddenList((Player) cs);
            }else if(game instanceof ManhuntGame){
                ManhuntGame manhunt = (ManhuntGame) game;
                manhunt.sendHiddenList((Player) cs);
            }
        }
    }
}