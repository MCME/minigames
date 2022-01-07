package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HaSHiddenList extends AbstractGameCommand{

    public HaSHiddenList(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.HIDE_AND_SEEK;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && game.isInGame((Player) cs) && isCorrectGameType((Player) cs, game, GameType.HIDE_AND_SEEK)) {
            HideAndSeekGame hidegame = (HideAndSeekGame) game;
            hidegame.sendHiddenList((Player) cs);
            }
        }
    }
