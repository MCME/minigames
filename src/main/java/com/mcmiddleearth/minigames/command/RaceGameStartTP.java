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

public class RaceGameStartTP extends AbstractGameCommand{

    public RaceGameStartTP(String... permissionNodes){
        super(0,true,permissionNodes);
        cmdGroup = CmdGroup.RACE;
        setShortDescription("TPs Manager to start point");
        setUsageDescription("Can be used to teleport to a race start directly after loading the race, that it you don´t need to find it.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame racegame = (RaceGame) game;
            racegame.TpToStart(((Player) cs));
        }
    }
}