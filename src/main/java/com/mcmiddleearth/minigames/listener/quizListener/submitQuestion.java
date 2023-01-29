package com.mcmiddleearth.minigames.listener.quizListener;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.quiz.QuizShowCategories;
import com.mcmiddleearth.minigames.quiz.QuizSubmitQuestion;
import com.mcmiddleearth.minigames.quiz.question.AbstractQuestion;
import com.mcmiddleearth.minigames.quiz.question.NumberQuestion;
import com.mcmiddleearth.minigames.quiz.question.QuestionType;
import com.mcmiddleearth.minigames.util.NumericUtil;
import com.mcmiddleearth.minigames.util.PluginData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Jubo
 */
public class submitQuestion implements Listener {

    @EventHandler
    public void askQuestionHandler(ChatEvent event){
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        if(message.startsWith("/"))
            return;
        if(!QuizSubmitQuestion.inConversation(sender))
            return;
        if(message.equals("!cancel")){
            QuizSubmitQuestion.cancel(sender);
            PluginData.getMessageUtil().sendInfoMessage(sender,"You cancelled creating a new question.");
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        QuizSubmitQuestion submit = QuizSubmitQuestion.getSubmitInstance(sender);
        McmeCommandSender wrappedSender = MiniGamesPlugin.wrapCommandSender(sender);
        switch(submit.getConType()){
            case QUESTION:
                submit.setQuestionText(message);
                if(submit.getType() == QuestionType.MULTI || submit.getType() == QuestionType.SINGLE) {
                    submit.setConType(QuestionConversationType.AANSWER);
                    sender.sendMessage(new ComponentBuilder(message + "\n[Choice A]").color(ChatColor.AQUA).create());
                }
                else if(submit.getType() == QuestionType.NUMBER) {
                    submit.setConType(QuestionConversationType.NUMBERANSWER);
                    sender.sendMessage(new ComponentBuilder(ChatColor.AQUA + message + ChatColor.DARK_GREEN + "\n[Hint] Type in chat a whole number.").create());
                }
                else {
                    submit.setConType(QuestionConversationType.FREEANSWER);
                    sender.sendMessage(new ComponentBuilder(ChatColor.AQUA + message + ChatColor.DARK_GREEN + "\n[Hint] Type your answer in chat.").color(ChatColor.DARK_GREEN).create());
                }
                break;
            case FREEANSWER:
                submit.setFreeText(message);
                submit.setConType(QuestionConversationType.CATEGORIES);
                sender.sendMessage(new ComponentBuilder(ChatColor.AQUA + message + ChatColor.DARK_GREEN + "\nCategories for quiz questions:").create());
                QuizShowCategories.execute(wrappedSender);
                break;
            case NUMBERANSWER:
                if (!NumericUtil.isInt(message)) {
                    PluginData.getMessageUtil().sendErrorMessage(sender, "The input must be a number");
                }else{
                    submit.setNumberText(Integer.parseInt(message));
                    submit.setConType(QuestionConversationType.TOLERANCE);
                    sender.sendMessage(new ComponentBuilder(ChatColor.AQUA + message + ChatColor.DARK_GREEN + "\nEnter the tolerance for an answer to be called correct.").create());
                }
                break;
            case TOLERANCE:
                if (!NumericUtil.isInt(message)) {
                    PluginData.getMessageUtil().sendErrorMessage(sender, "The input must be a number");
                }else{
                    submit.setDeviation(Integer.parseInt(message));
                    submit.setConType(QuestionConversationType.CATEGORIES);
                    sender.sendMessage(new ComponentBuilder(ChatColor.AQUA + message + ChatColor.DARK_GREEN + "\nCategories for quiz questions:").create());
                    QuizShowCategories.execute(wrappedSender);
                }
                break;
            case AANSWER:
                submit.setaText(message);
                submit.setConType(QuestionConversationType.BANSWER);
                sender.sendMessage(new ComponentBuilder(message+"\n[Choice B]").color(ChatColor.AQUA).create());
                break;
            case BANSWER:
                submit.setbText(message);
                submit.setConType(QuestionConversationType.CANSWER);
                sender.sendMessage(new ComponentBuilder(message+"\n[Choice C]").color(ChatColor.AQUA).create());
                break;
            case CANSWER:
                submit.setcText(message);
                submit.setConType(QuestionConversationType.DANSWER);
                sender.sendMessage(new ComponentBuilder(message+"\n[Choice D]").color(ChatColor.AQUA).create());
                break;
            case DANSWER:
                submit.setdText(message);
                submit.setConType(QuestionConversationType.CORRECTANSWER);
                sender.sendMessage(new ComponentBuilder(message+"\n[Hint] Type in chat the letter of the correct answer.").color(ChatColor.AQUA).create());
                break;
            case CORRECTANSWER:
                if(submit.getType() == QuestionType.SINGLE && message.length() != 1 ){
                    PluginData.getMessageUtil().sendErrorMessage(sender,"Try again. Only one answer is correct.");
                    return;
                }else if(submit.getType() == QuestionType.MULTI && message.length() > 4){
                    PluginData.getMessageUtil().sendErrorMessage(sender,"Try again. Wrong submission.");
                    return;
                }
                submit.setCorrectAnswer(message);
                submit.setConType(QuestionConversationType.CATEGORIES);
                sender.sendMessage(new ComponentBuilder(ChatColor.AQUA + message + ChatColor.DARK_GREEN + "\nCategories for quiz questions:").create());
                QuizShowCategories.execute(wrappedSender);
                break;
            case CATEGORIES:
                submit.setCategories(message);
                submit.saveQuestion();
                sender.sendMessage(new ComponentBuilder(ChatColor.AQUA+message).create());
                PluginData.getMessageUtil().sendInfoMessage(sender,"You submitted a new question.");
                QuizSubmitQuestion.cancel(sender);
                break;
        }
    }
}
