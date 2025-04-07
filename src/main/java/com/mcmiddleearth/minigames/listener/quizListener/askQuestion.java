package com.mcmiddleearth.minigames.listener.quizListener;

import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.velocity.question.AbstractQuestion;
import com.mcmiddleearth.minigames.velocity.question.NumberQuestion;
import com.mcmiddleearth.minigames.velocity.question.SingleChoiceQuestion;
import com.mcmiddleearth.minigames.spigot.util.PluginData;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import org.apache.commons.lang.StringUtils;

public class askQuestion implements EventHandler<PlayerChatEvent>
{

    /*
    TODO:
     rewrite
     */

    @Subscribe
    public void execute(PlayerChatEvent event){
        Player sender = event.getPlayer();
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
        //event.setCancelled(true);
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
        //sender.sendMessage(new ComponentBuilder("[Your answer] "+answer).color(ChatColor.AQUA).create());
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
