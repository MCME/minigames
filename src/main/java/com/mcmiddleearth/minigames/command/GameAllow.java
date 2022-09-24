/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameAllow extends AbstractGameCommand{
    
    public GameAllow(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Allows various actions for a game.");
        setUsageDescription(" flight|teleport|join|warp|spectate|collision|invisible|signs: 'flight'/'teleport' allows for players in the game to fly or teleport. 'join' allows players to join without invitation. 'warp' allows player to warp to game location. 'spectate' allows players to see scoreboard of the game without participating.'collision' allows players to collide in games.'save' allows /game tpcp in races.'invisible' allows Invisibility in races.'signs' allow signs fr GeoGuessr");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game!=null && isManager((Player) cs, game)) {
            if(args[0].equalsIgnoreCase("warp")) {
                game.setWarpAllowed(true);
                sendWarpAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("join")) {
                game.setPrivat(false);
                sendJoinAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("spectate")) {
                game.setSpectateAllowed(true);
                sendSpectateAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("flight")) {
                game.setFlightAllowed(true);
                sendFlightAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("teleport")) {
                if(game instanceof HideAndSeekGame) {
                    sentNotPossibleMessage(cs);
                    // return;
                }
                game.setTeleportAllowed(true);
                sendTeleportAllowedMessage(cs);
            }
            else if(args[0].equalsIgnoreCase("collision")) {
                game.setCollision(true);
                sendCollisionMessage(cs);
            }
            else if(args[0].equalsIgnoreCase("save")) {
                if(game instanceof RaceGame){
                    RaceGame racegame = (RaceGame) game;
                    racegame.setSave(true);
                    sendSaveMessage(cs);
                }else{
                    sendNotPossibleMessage(cs);
                }
            }
            else if(args[0].equalsIgnoreCase("invisible")){
                if(game instanceof RaceGame){
                    RaceGame racegame = (RaceGame) game;
                    racegame.setInvisbile(true);
                    sendInvisibleMessage(cs);
                }else{
                    sendNotPossibleMessage(cs);
                }
            }
            else if(args[0].equalsIgnoreCase("signs")){
                if(game instanceof GeoGuessrGame){
                    GeoGuessrGame geogame = (GeoGuessrGame) game;
                    geogame.setSigns(false);
                    sendSigns(cs);
                }else{
                    sendNotPossibleMessage(cs);
                }
            }
            else if(args[0].equalsIgnoreCase("points")){
                if(game instanceof GeoGuessrGame){
                    GeoGuessrGame geogame = (GeoGuessrGame) game;
                    geogame.setPoints(true);
                    sendPoints(cs);
                }else{
                    sendNotPossibleMessage(cs);
                }
            }
            else if(args[0].equalsIgnoreCase("throwables")){
                if(game instanceof WerewolfGame){
                    WerewolfGame werewolf = (WerewolfGame) game;
                    werewolf.switchThrowable(true);
                }else{
                    sendNotPossibleMessage(cs);
                }
            }
            else {
                sendInvalidArgumentMessage(cs);
            }
        }
    }
    
    private void sendWarpAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed all players to warp to this game.");
    }

    private void sendJoinAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed all players to join this game.");
    }
    
    private void sendSpectateAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed all players to spectate to this game.");
    }

    private void sendFlightAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed players of this game to fly.");
    }

    private void sendTeleportAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed players of this game to teleport (using commands like /tpa and /warp).");
    }

    private void sendCollisionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed player collision in this game.");
    }

    private void sendInvalidArgumentMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Argument.");
    }

    private void sendSaveMessage(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed player save in this game.");
    }

    private void sendInvisibleMessage(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs, "You allowed player to be invisible in this race.");
    }

    private void sentNotPossibleMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "It is not possible to allow teleport in a "
                                         +PluginData.getMessageUtil().ERROR_STRESSED+"Hide and Seek"
                                         +PluginData.getMessageUtil().ERROR+" game.");
    }

    private void sendSigns(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You switched signs in GeoGuessr on.");
    }

    private void sendPoints(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You switched points in GeoGuessr to more for the first.");
    }

    private void sendThrowable(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You enabled throwables.");
    }

    private void sendNotPossibleMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "This is not possible.");
    }
}
