package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.ManhuntGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */
public class ManhuntGameHunterList extends AbstractGameCommand{

    public ManhuntGameHunterList(String... permissionNodes){
        super(0,true,permissionNodes);
        setShortDescription("Shows all hunters.");
        setUsageDescription("/game hunterlist. Can give everyone in the game a list of all hunters.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isCorrectGameType((Player)cs,game, GameType.MANHUNT)){
            ManhuntGame manhunt = (ManhuntGame) game;
            manhunt.sendSeekerList((Player)cs);
        }
    }
}
