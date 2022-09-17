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
public class WerewolfGameList extends AbstractGameCommand{

    public WerewolfGameList(String... permissionNodes){
        super(0,true,permissionNodes);
        cmdGroup = CmdGroup.WEREWOLF;
        setShortDescription("list of players in werewolf");
        setUsageDescription("/game list; Gives a list of eliminated and alive players in werewolf");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)){
            WerewolfGame werewolf = (WerewolfGame) game;
            werewolf.sendAliveList(cs);
            werewolf.sendEliminatedList(cs);
        }
    }
}
