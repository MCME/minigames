package com.mcmiddleearth.minigames.listener.quizListener;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quiz.question.AbstractQuestion;
import com.mcmiddleearth.minigames.util.PluginData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class askQuestion implements Listener {

    @EventHandler
    public void askQuestionHandler(ChatEvent event){
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        if(message.startsWith("/"))
            return;
        if(!PluginData.isInGame(sender))
            return;
        if(!PluginData.isCorrectGameType(sender, GameType.LORE_QUIZ))
            return;
        QuizGame game = (QuizGame) PluginData.getGame(sender);
        if(!game.isInConversation(sender))
            return;
        String answer = event.getMessage();
        AbstractQuestion question = game.getCurrentQuestion();
        game.setAllAnswered();
        sender.sendMessage(new ComponentBuilder("[Your answer] "+answer).color(ChatColor.AQUA).create());
        if(question.isCorrectAnswer(answer)){
            game.incrementScore(sender);
            PluginData.getMessageUtil().sendInfoMessage(sender,"You answered this Question correctly.");
        }else{
            PluginData.getMessageUtil().sendInfoMessage(sender,"You failed to answer this Question correctly. Correct answer: "+question.getCorrectAnswer());
        }
        event.setCancelled(true);
    }
}
