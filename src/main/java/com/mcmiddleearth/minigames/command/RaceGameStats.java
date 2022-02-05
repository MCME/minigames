package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */

public class RaceGameStats extends AbstractGameCommand{

    public RaceGameStats(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.RACE;
        setShortDescription(": Shows your PB for the race");
        setUsageDescription(": Gives you infos about your personal best time for the loaded race.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && game.isInGame((Player) cs) && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame racegame = (RaceGame) game;
            racegame.getStats((Player)cs);
        }
    }

}