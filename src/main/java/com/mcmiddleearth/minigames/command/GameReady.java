/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GeoGuessrGame;
import com.mcmiddleearth.minigames.game.GolfGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameReady extends AbstractGameCommand{
    
    public GameReady(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Announces a game.");
        setUsageDescription(": Announces a game which is sending a message to all online players.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            if(game.isAnnounced()) {
                sendAlreadyAnnouncedErrorMessage(cs);
            }
            else {
                if(game instanceof RaceGame) {
                    RaceGame raceGame = (RaceGame) game; 
                    if(!raceGame.hasStart() || !raceGame.hasFinish()) {
                        sendRaceNoStartFinishMessage(cs);
                        return;
                    }
                    raceGame.setHighscore();
                }

                if(game instanceof GolfGame) {
                    GolfGame golfGame = (GolfGame) game;
                    if(!golfGame.hasTeeStart() || !golfGame.hasTeeEnd() || !golfGame.hasHoleStart() || !golfGame.hasHoleEnd()) {
                        sendNoGolfStartFinishMessage(cs);
                        return;
                    }

                    if(!golfGame.hasEnoughTees()) {
                        sendNotEnoughTeesMessage(cs);
                        return;
                    }

                    if(!golfGame.hasEnoughHoles()) {
                        sendNotEnoughHolesMessage(cs);
                        return;
                    }
                }
                if(game instanceof GeoGuessrGame){
                    GeoGuessrGame geogame = (GeoGuessrGame) game;
                    geogame.getXWarps();
                }
                game.announceGame();
                game.addPlayer((Player) cs);
                PluginData.setGameChat((Player) cs,true);
                sendPlayerJoinMessage(cs, game);
            }
        }
    }

    public void sendPlayerJoinMessage(CommandSender cs, AbstractGame game) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You joined the minigame "+ game.getName()
                +". For conversations please use the game chat with "+PluginData.getMessageUtil().STRESSED+"/gc <message>");
        GameChatUtil.sendAllInfoMessage(cs, game, cs.getName()+" joined the game.");
    }

    private void sendRaceNoStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need to set a start and finish before announcing a race game. Also please remember that you can't add checkpoints after announcing a race.");
    }

    private void sendNoGolfStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a start tee or hole/end tee or hole.");
    }

    private void sendNotEnoughTeesMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a tee amount of 9, 18, 27, 14 or 20.");
    }

    private void sendNotEnoughHolesMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a hole amount of 9, 18, 27, 14 or 20.");
    }
}
