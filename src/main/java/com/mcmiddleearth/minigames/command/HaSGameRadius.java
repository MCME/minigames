package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.pluginutil.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HaSGameRadius extends AbstractGameCommand{

    public HaSGameRadius(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.HIDE_AND_SEEK;
        setShortDescription("Changes the radius");
        setUsageDescription("/game radius <number> can change the radius in Hide and Seek during the game");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player) cs,game, GameType.HIDE_AND_SEEK)){
            HideAndSeekGame hidegame = (HideAndSeekGame) game;
            int radius = StringUtil.parseInt(args[0]);
            hidegame.setRadius(radius);
            hidegame.sendRadiusMessage();
        }
    }
}
