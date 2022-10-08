package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jubo
 */
public class GameSwitchables extends AbstractGameCommand{

    public GameSwitchables(String... permissionNodes){
        super(0,true,permissionNodes);
        setShortDescription("GUI for allow/deny");
        setUsageDescription("/game switchables. Written Books are enabled things and books are disabled things");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game)){
            game.openGUI_Switchables((Player)cs);
        }
    }
}
