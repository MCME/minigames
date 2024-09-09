package com.mcmiddleearth.minigames.bungee.listener;

import com.mcmiddleearth.minigames.core.game.AbstractGame;
import com.mcmiddleearth.minigames.core.game.QuizGame;
import com.mcmiddleearth.minigames.core.game.RaceGame;
import com.mcmiddleearth.minigames.core.util.PluginData;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfirmationListener implements Listener {

    @EventHandler
    public void confirmationHandler(ChatEvent event){
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        if(message.startsWith("/"))
            return;
        AbstractGame game = PluginData.getGame(sender);
        if(game == null)
            return;

        if(!AbstractGame.getInConversation().contains(sender)){
            return;
        }
        event.setCancelled(true);
        if (message.equalsIgnoreCase("yes")) {
                if(game instanceof QuizGame){
                    QuizGame quizGame = (QuizGame) game;
                    if(quizGame.saveConversation()){
                        try {
                            quizGame.saveQuestionsToJson(quizGame.getSaveFile(), quizGame.getSaveDescription());
                            PluginData.getMessageUtil().sendInfoMessage(sender, "Questions of the game were saved to disk.");
                        } catch (IOException ex) {
                            PluginData.getMessageUtil().sendErrorMessage(sender, "There was an error. Nothing was saved.");
                            Logger.getLogger(ConfirmationListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else if(quizGame.acceptConversation()){
                        try{
                            quizGame.saveQuestionsToDataFile(PluginData.getQuestionDataTable());
                            PluginData.getMessageUtil().sendInfoMessage(sender,"Questions of the quiz were accepted. Do /game end when done.");
                        } catch (IOException ex) {
                            PluginData.getMessageUtil().sendErrorMessage(sender,"There was an error. Nothing was saved.");
                            Logger.getLogger(ConfirmationListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }else if(game instanceof RaceGame){

                }
                if(AbstractGame.deleteConversation()){
                    if((AbstractGame.getDeleteFile()).delete())
                        PluginData.getMessageUtil().sendInfoMessage(sender, "File deleted.");
                    else
                        PluginData.getMessageUtil().sendErrorMessage(sender, "There was an error deleting the file.");
                }
            } else if (message.equalsIgnoreCase("no")) {
                PluginData.getMessageUtil().sendErrorMessage(sender, "Cancelled.");
                event.setCancelled(true);
            } else {
                PluginData.getMessageUtil().sendErrorMessage(sender, "Please type yes or no.");
                return;
            }
            if(game instanceof QuizGame)
                ((QuizGame) game).setAcceptConversation(false);
            AbstractGame.setDeleteConversation(false);
            game.setSaveConversation(false);
            AbstractGame.removeConversation(sender);
    }
}
