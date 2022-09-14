/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.*;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 *
 * @author Eriol_Eandur
 */
public class GameCreate extends AbstractGameCommand{
    
    public GameCreate(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Creates a new mini game.");
        setUsageDescription(" quiz|race|hide|golf|pvp|geo <gamename>: Creates a lore quiz or a race or a hide and seek or golf or a geoguessr game with name <gamename>. The location of the player issuing the command becomes the warp of the game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!isAlreadyInGame((Player)cs) && !isAlreadyManager((Player) cs)) {
            if(PluginData.getGame(args[1]) != null) {
                sendGameExistsMessage(cs);
                return;
            }
            AbstractGame game;
            GameType type = GameType.getGameType(args[0]);
            //Golf is not finished and PVP is currently broken
            if(args[0].equalsIgnoreCase("golf") || args[0].equalsIgnoreCase("pvp")){
                sendCurrentlyDeactivaed(cs,args[0]);
                return;
            }
            if(type==null) {
                sendInvalidGameTypeErrorMessage(cs);
                return;
            }
            UUID uuid = ((Player) cs).getUniqueId();
            switch(type) {
                case HIDE_AND_SEEK:
                    PluginData.stopSpectating((Player)cs);
                    game = new HideAndSeekGame((Player) cs, args[1]);
                    game.addPlayer((Player) cs);
                    PluginData.setGameChat((Player) cs,true);
                    sendPlayerJoinMessage(cs, game);
                    break;
                case RACE:
                    PluginData.stopSpectating((Player)cs);
                    game = new RaceGame((Player) cs, args[1]);
                    sendRaceGameCreateMessage(cs);
                    break;
                case LORE_QUIZ:
                    PluginData.stopSpectating((Player)cs);
                    game = new QuizGame((Player) cs, args[1]);
                    sendQuizGameCreateMessage(cs);
                    break;
                case GOLF:
                    PluginData.stopSpectating((Player)cs);
                    game = new GolfGame((Player) cs, args[1]);
                    sendGolfGameCreateMessage(cs);
                    break;
                case PVP:
                    PluginData.stopSpectating((Player)cs);
                    game = new PvPGame((Player) cs, args[1]);
                    sendPvPGameCreateMessage(cs);
                    break;
                case GEO_GUESSR:
                    PluginData.stopSpectating((Player)cs);
                    game = new GeoGuessrGame((Player) cs, args[1]);
                    sendGeoGuessrGameCreateMessage(cs);
                    break;
                case CATCH:
                    return;
                    /*
                    PluginData.stopSpectating((Player)cs);
                    game = new CatchGame((Player)cs,args[1]);
                    game.addPlayer((Player) cs);
                    PluginData.setGameChat((Player) cs,true);
                    sendPlayerJoinMessage(cs, game);
                    break;
                     */
                case WEREWOLF:
                    PluginData.stopSpectating((Player)cs);
                    game = new WerewolfGame((Player)cs,args[1]);
                    //sendWerewolfGameCreateMessage(cs);
                    game.addPlayer((Player)cs);
                    PluginData.setGameChat((Player)cs,true);
                    sendPlayerJoinMessage(cs,game);
                    break;
                case MANHUNT:
                    if(String.valueOf(uuid).equalsIgnoreCase("4a4a85b0-0d8f-425b-ae25-4900f017ac89")
                            || String.valueOf(uuid).equalsIgnoreCase("b8d1ce5c-2b38-428c-9bb8-c8ee6ad58c4b")
                            || String.valueOf(uuid).equalsIgnoreCase("975c816e-ebb3-4cd5-bec1-394e0976b6f7")
                            || String.valueOf(uuid).equalsIgnoreCase("5614724c-33f2-464c-9e9f-1c593b416bd8")
                            || String.valueOf(uuid).equalsIgnoreCase("08ded767-ace9-421c-9776-b37c28bf2020")){
                        PluginData.stopSpectating((Player)cs);
                        game = new ManhuntGame((Player)cs,args[1]);
                        game.addPlayer((Player)cs);
                        PluginData.setGameChat((Player)cs,true);
                        sendPlayerJoinMessage(cs,game);
                        break;
                    }else{
                        sendCurrentlyDeactivaed(cs,"Manhunt");
                        return;
                    }
                default:
                    sendInvalidGameTypeErrorMessage(cs);
                    return;
            }
            if(args.length>2 && args[2].equalsIgnoreCase("private")) {
                game.setPrivat(true);
            }
            PluginData.addGame(game);
        }
    }

    public void sendPlayerJoinMessage(CommandSender cs, AbstractGame game) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You joined the minigame "+ game.getName()
                +". For conversations please use the game chat with "+PluginData.getMessageUtil().STRESSED+"/gc <message>");
        GameChatUtil.sendAllInfoMessage(cs, game, cs.getName()+" joined the game.");
    }

    public void sendQuizGameCreateMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new Lore Quiz game.");
    }

    private void sendInvalidGameTypeErrorMessage(CommandSender cs) {
         PluginData.getMessageUtil().sendErrorMessage(cs, "You specified an invalid game type.");
    }
    
    private void sendGameExistsMessage(CommandSender cs) {
         PluginData.getMessageUtil().sendErrorMessage(cs, "A game with that name already exists.");
    }

    private void sendRaceGameCreateMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new Race game.");
    }

    private void sendGolfGameCreateMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new Golf game.");
    }

    private void sendPvPGameCreateMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new PvP game.");
    }

    private void sendGeoGuessrGameCreateMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new GeoGuessr game.");
    }

    private void sendWerewolfGameCreateMessage(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new Werewolf game.");
    }

    private void sendCurrentlyDeactivaed(CommandSender cs, String name){
        PluginData.getMessageUtil().sendErrorMessage(cs,name+" is currently deactived.");
    }
 }
