package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RaceGameDeleteHighscore extends AbstractGameCommand{

    public RaceGameDeleteHighscore(String... permissionNodes){
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.RACE;
        setShortDescription("Resets Highscore");
        setUsageDescription("resets the highscore of the loaded race, for example when replanned or unwanted person");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && game.isInGame((Player) cs) && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame racegame = (RaceGame) game;
            racegame.resetHighscore((Player)cs);
        }
    }
}

