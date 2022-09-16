package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WerewolfGameList extends AbstractGameCommand{

    public WerewolfGameList(String... permissionNodes){
        super(0,true,permissionNodes);
        cmdGroup = CmdGroup.WEREWOLF;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)){
            WerewolfGame werewolf = (WerewolfGame) game;
            werewolf.list(cs);
        }
    }
}
