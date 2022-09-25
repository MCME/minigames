package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WereWolfGameRoleInfo extends AbstractGameCommand{

    public WereWolfGameRoleInfo(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.WEREWOLF;
        setShortDescription("/game showinfo rolename");
        setUsageDescription("Gives you the rolebook for the requested role.");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        AbstractGame game = getGame((Player)cs);
        if(game != null && isCorrectGameType((Player)cs,game, GameType.WEREWOLF)){
            WerewolfGame werewolf = (WerewolfGame) game;
            boolean exists;
            String argsAdded = "";
            if(args.length > 1){
                for(int i = 0;i < args.length; i++){
                    argsAdded = argsAdded + " " + args[i];
                }
                argsAdded = argsAdded.substring(1);
                exists = werewolf.sendRoleBook((Player)cs,argsAdded);
            }else{
                argsAdded = args[0];
                exists = werewolf.sendRoleBook((Player)cs,argsAdded);
            }
            if(exists){
                sendRoleSent(cs,argsAdded);
            }else{
                sendRoleNotFound(cs,argsAdded);
            }
        }
    }

    private void sendRoleNotFound(CommandSender cs, String role){
        PluginData.getMessageUtil().sendErrorMessage(cs,role+" does not exist.");
    }

    private void sendRoleSent(CommandSender cs, String role){
        PluginData.getMessageUtil().sendInfoMessage(cs,role+" was sent to you.");
    }
}
