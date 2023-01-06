/*
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MicroGame.AbstractMicroGame;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.MicroGame.JumpGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameJump extends AbstractGameCommand{

    public GameJump(String... permissionNodes){
        super(0,true,permissionNodes);
        cmdGroup = CmdGroup.JUMP;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs,String... args){
        PluginData.stopSpectating((Player)cs);
        AbstractMicroGame game = new JumpGame((Player)cs);
        PluginData.setGameChat((Player)cs,true);
    }
}
 */
