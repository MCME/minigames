package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WerewolfGameConfiguration extends AbstractGameCommand{

    public WerewolfGameConfiguration(String... permissionNodes){
        super(0,true,permissionNodes);
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        WerewolfGame werewolf = (WerewolfGame) game;
        werewolf.Configuration((Player) cs);
    }
}
