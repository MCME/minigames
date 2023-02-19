package com.mcmiddleearth.minigames.listener.quizListener;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quiz.question.AbstractQuestion;
import com.mcmiddleearth.minigames.quiz.question.NumberQuestion;
import com.mcmiddleearth.minigames.quiz.question.SingleChoiceQuestion;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.lang.StringUtils;

public class askQuestion implements Listener {

    /*
    TODO:
     handle input of different types of questions   x
     write texts in sendQuestionToPlayer()          x
     */

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
        event.setCancelled(true);
        String answer = event.getMessage();
        AbstractQuestion question = game.getCurrentQuestion();
        if(question instanceof SingleChoiceQuestion){
            if(answer.length() != 1){
                PluginData.getMessageUtil().sendErrorMessage(sender,"Invalid answer. You did not type in a single answer letter.");
                return;
            }
        }else if(question instanceof NumberQuestion){
            if(!StringUtils.isNumeric(answer)){
                PluginData.getMessageUtil().sendErrorMessage(sender,"Invalid answer. You did not type in a whole number.");
                return;
            }
        }
        game.removeQuizConversation(sender);
        sender.sendMessage(new ComponentBuilder("[Your answer] "+answer).color(ChatColor.AQUA).create());
        if(question.isCorrectAnswer(answer)){
            game.incrementScore(sender);
            if(question instanceof NumberQuestion){
                if(!answer.equals(question.getCorrectAnswer()))
                    PluginData.getMessageUtil().sendInfoMessage(sender,"Almost! The right answer was "+question.getCorrectAnswer()+" but you were close enough.");
                else
                    PluginData.getMessageUtil().sendInfoMessage(sender,"You answered this Question correctly.");
            }else
                PluginData.getMessageUtil().sendInfoMessage(sender,"You answered this Question correctly.");
        }else{
            if(question instanceof NumberQuestion){
                PluginData.getMessageUtil().sendInfoMessage(sender,"You failed to answer this Question correctly. Correct answer was "
                        +question.getCorrectAnswer()+". Allowed deviation from correct answer was "+((NumberQuestion)question).getPrecision()+".");
            }else
                PluginData.getMessageUtil().sendInfoMessage(sender,"You failed to answer this Question correctly. Correct answer: "+question.getCorrectAnswer());
        }
        if(game.allAnswered())
            game.setAllAnswered();
    }
}
