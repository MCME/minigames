package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */
public class WerewolfGameConfiguration extends AbstractGameCommand{

    public WerewolfGameConfiguration(String... permissionNodes){
        super(0,true,permissionNodes);
        setShortDescription("/game config ");
        setUsageDescription("Lets the game manager configurate the roles for the werewolf game.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)) {
            WerewolfGame werewolf = (WerewolfGame) game;
            werewolf.Configuration((Player) cs);
        }
    }
}
