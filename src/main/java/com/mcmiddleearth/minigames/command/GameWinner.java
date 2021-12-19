package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GeoGuessrGame;
import com.mcmiddleearth.minigames.game.QuizGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */

public class GameWinner extends AbstractGameCommand{

    public GameWinner(String... permissionNodes){
        super(0,true,permissionNodes);
        setShortDescription("Announces the winner");
        setUsageDescription("Announces the winner in GeoGuessr Games and Quiz Games. Has to be used if there are two or more winner.");
    }

    @Override
    protected void execute(CommandSender cs, String... agrs){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game)){
            if(isCorrectGameType((Player) cs,game, GameType.GEO_GUESSR)){
                GeoGuessrGame geogame = (GeoGuessrGame) game;
                geogame.GeoGameWinner((Player) cs);
            }else if(isCorrectGameType((Player) cs,game, GameType.LORE_QUIZ)){
                QuizGame quizgame = (QuizGame) game;
                quizgame.QuizGameWinner((Player) cs);
            }
        }
    }
}
