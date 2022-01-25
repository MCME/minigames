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

public class RaceGameTPCheckpoint extends AbstractGameCommand{

    public RaceGameTPCheckpoint(String... permissionNodes){
        super(0,true,permissionNodes);
        cmdGroup = CmdGroup.RACE;
        setShortDescription("Teleport to last checkpoint");
        setUsageDescription("Lets the user teleport to the last checkpoint, but only one time, like a last chance save.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && game.isInGame((Player) cs) && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame racegame = (RaceGame) game;
            racegame.tp_Save((Player)cs);
        }
    }
}
