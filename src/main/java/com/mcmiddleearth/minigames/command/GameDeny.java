/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GeoGuessrGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.game.WerewolfGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameDeny extends AbstractGameCommand{
    
    public GameDeny(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Denies various actions for a game.");
        setUsageDescription(" flight|teleport|join|warp|save|collision|invisible|signs|glow: 'flight' or 'teleport' denies for players in the game to fly or teleport. " +
                "'join' denies players to join without invitation. 'warp' denies players to warp to game location. 'spectate' denies players to see the game scoreboad without participating. " +
                "'collision' denies players to collide in games.'save' denies /game tpcp in races.'invisible' denies Invisibility in races.'signs' removes Signs in GeoGuessr" +
                "/game glow to activate the glow effect on all players");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game!=null && isManager((Player) cs, game)) {
            if (args[0].equalsIgnoreCase("warp")) {
                game.setWarpAllowed(false);
                sendWarpAllowedMessage(cs);
            } else if (args[0].equalsIgnoreCase("join")) {
                game.setPrivat(true);
                sendJoinAllowedMessage(cs);
            } else if (args[0].equalsIgnoreCase("spectate")) {
                game.setSpectateAllowed(false);
                sendSpectateAllowedMessage(cs);
            } else if (args[0].equalsIgnoreCase("flight")) {
                game.setFlightAllowed(false);
                sendFlightAllowedMessage(cs);
            } else if (args[0].equalsIgnoreCase("teleport")) {
                game.setTeleportAllowed(false);
                sendTeleportAllowedMessage(cs);
            } else if (args[0].equalsIgnoreCase("Collision")) {
                game.setCollision(false);
                sendCollisionMessage(cs);
            } else if (args[0].equalsIgnoreCase("save")) {
                if (game instanceof RaceGame) {
                    game.setTPSave(false);
                    sendSaveMessage(cs);
                } else {
                    sendNotPossibleMessage(cs);
                }
            } else if (args[0].equalsIgnoreCase("invisible")) {
                if (game instanceof RaceGame) {
                    game.setInvisible(false);
                    sendInvisibleMessage(cs);
                } else {
                    sendNotPossibleMessage(cs);
                }
            } else if (args[0].equalsIgnoreCase("signs")) {
                if (game instanceof GeoGuessrGame) {
                    game.setSigns(true);
                    sendSigns(cs);
                } else {
                    sendNotPossibleMessage(cs);
                }
            } else if (args[0].equalsIgnoreCase("points")) {
                if (game instanceof GeoGuessrGame) {
                    game.setPoints(false);
                    sendPoints(cs);
                } else {
                    sendNotPossibleMessage(cs);
                }
            } else if (args[0].equalsIgnoreCase("throwable")) {
                if (game instanceof WerewolfGame) {
                    game.setThrowable(false);
                    sendThrowable(cs);
                } else {
                    sendNotPossibleMessage(cs);
                }
            }else if(args[0].equalsIgnoreCase("glow")){
                game.setGlow(false);
                sendGlow(cs);
            } else {
                sendInvalidArgumentMessage(cs);
            }
        }
    }
    
    private void sendWarpAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You denied all players to warp to this game.");
    }

    private void sendJoinAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Only invited players may join the game now.");
    }

    private void sendSpectateAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You denied all players to spectate to this game.");
    }
    private void sendFlightAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You denied players of this game to fly.");
    }

    private void sendTeleportAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You denied players of this game to teleport (using commands like /tpa and /warp).");
    }
    private void sendCollisionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You denied player collision in this game.");
    }

    private void sendSaveMessage(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs, "You denied player save in this game.");
    }

    private void sendInvisibleMessage(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs, "You denied player to be invisible in this race.");
    }

    private void sendInvalidArgumentMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Argument.");
    }

    private void sendSigns(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You switched signs in GeoGuessr off.");
    }

    private void sendPoints(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You switched points in GeoGuessr to equal.");
    }

    private void sendThrowable(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You disabled throwables.");
    }

    private void sendGlow(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You disabled the glow effect.");
    }

    private void sendNotPossibleMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "This is not possible.");
    }
}