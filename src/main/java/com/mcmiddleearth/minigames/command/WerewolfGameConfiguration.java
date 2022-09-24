package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import com.mcmiddleearth.minigames.werewolf.WerewolfRoles;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WerewolfGameConfiguration extends AbstractGameCommand{

    public WerewolfGameConfiguration(String... permissionNodes){
        super(0,true,permissionNodes);
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isManager((Player)cs,game) && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)) {
            WerewolfGame werewolf = (WerewolfGame) game;
            werewolf.Configuration((Player) cs);
        }

        /*
        WerewolfRoles roles = new WerewolfRoles();
        Map<String,Object> role = new HashMap<>();
        role = roles.getRoles();
        cs.sendMessage(role.toString());

         */
    }
}
