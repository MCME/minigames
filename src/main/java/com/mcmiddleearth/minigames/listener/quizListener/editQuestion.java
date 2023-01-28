package com.mcmiddleearth.minigames.listener.quizListener;

import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quiz.QuizEditQuestion;
import com.mcmiddleearth.minigames.quiz.QuizSubmitQuestion;
import com.mcmiddleearth.minigames.quiz.question.QuestionType;
import com.mcmiddleearth.minigames.util.NumericUtil;
import com.mcmiddleearth.minigames.util.PluginData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Chat;

/**
 * @author Jubo
 */
public class editQuestion implements Listener {

    @EventHandler
    public void askQuestionHandler(ChatEvent event){
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        if(message.startsWith("/"))
            return;
        if(!QuizEditQuestion.inConversation(sender))
            return;
        if(message.equals("!cancel")){
            QuizEditQuestion.cancel(sender);
            PluginData.getMessageUtil().sendInfoMessage(sender,"You cancelled editing this question.");
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        QuizEditQuestion edit = QuizEditQuestion.getEditInstance(sender);
        switch(edit.getPosition()){
            case "question":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Question kept.");
                }else {
                    edit.setQuestion(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                if(edit.getType() == QuestionType.FREE){
                    edit.setPosition("freeAnswer");
                    sender.sendMessage(new ComponentBuilder("[Answer] "+edit.getFreeAnswer()).color(ChatColor.DARK_GREEN).create());
                }
                else if(edit.getType() == QuestionType.NUMBER){
                    edit.setPosition("numberAnswer");
                    sender.sendMessage(new ComponentBuilder("[Answer] "+edit.getNumberAnswer()).color(ChatColor.DARK_GREEN).create());
                }
                else{
                    edit.setPosition("aAnswer");
                    sender.sendMessage(new ComponentBuilder("[Choice A] "+edit.getaAnswer()).color(ChatColor.DARK_GREEN).create());
                }
                break;
            case "freeAnswer":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Answer kept.");
                }else {
                    edit.setFreeAnswer(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                edit.setPosition("categories");
                sendCategories(sender,edit);
                break;
            case "aAnswer":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Choice kept.");
                }else {
                    edit.setaAnswer(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                edit.setPosition("bAnswer");
                sender.sendMessage(new ComponentBuilder("[Choice B] "+edit.getbAnswer()).color(ChatColor.DARK_GREEN).create());
                break;
            case "bAnswer":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Choice kept.");
                }else {
                    edit.setbAnswer(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                edit.setPosition("cAnswer");
                sender.sendMessage(new ComponentBuilder("[Choice C] "+edit.getcAnswer()).color(ChatColor.DARK_GREEN).create());
                break;
            case "cAnswer":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Choice kept.");
                }else {
                    edit.setcAnswer(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                edit.setPosition("dAnswer");
                sender.sendMessage(new ComponentBuilder("[Choice D] "+edit.getdAnswer()).color(ChatColor.DARK_GREEN).create());
                break;
            case "dAnswer":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Choice kept.");
                }else {
                    edit.setdAnswer(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                edit.setPosition("correctAnswer");
                sender.sendMessage(new ComponentBuilder("[Correct Answer] "+edit.getCorrectAnswer()).color(ChatColor.DARK_GREEN).create());
                break;
            case "correctAnswer":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Correct choice kept.");
                }else {
                    if(edit.getType() == QuestionType.SINGLE && message.length() != 1 ){
                        PluginData.getMessageUtil().sendErrorMessage(sender,"Try again. Only one answer is correct.");
                        return;
                    }else if(edit.getType() == QuestionType.MULTI && message.length() > 4){
                        PluginData.getMessageUtil().sendErrorMessage(sender,"Try again. Wrong submission.");
                        return;
                    }
                    edit.setCorrectAnswer(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                edit.setPosition("categories");
                sendCategories(sender,edit);
                break;
            case "numberAnswer":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Choice kept.");
                }else {
                    if(NumericUtil.isInt(message)) {
                        edit.setNumberAnswer(NumericUtil.getInt(message));
                        sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                    }else{
                        PluginData.getMessageUtil().sendErrorMessage(sender, "The input must be a number");
                        return;
                    }
                }
                edit.setPosition("deviation");
                sender.sendMessage(new ComponentBuilder("[Tolerance] "+edit.getDeviation()).color(ChatColor.DARK_GREEN).create());
                break;
            case "deviation":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Choice kept.");
                }else {
                    if(NumericUtil.isInt(message)) {
                        edit.setDeviation(NumericUtil.getInt(message));
                        sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                    }else{
                        PluginData.getMessageUtil().sendErrorMessage(sender, "The input must be a number");
                        return;
                    }
                }
                edit.setPosition("categories");
                sendCategories(sender,edit);
                break;
            case "categories":
                if(message.equals("!keep")){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.YELLOW+"Categories are kept.");
                }else {
                    edit.setCategories(message);
                    sender.sendMessage(new ComponentBuilder(message).color(ChatColor.AQUA).create());
                }
                QuizEditQuestion.cancel(sender);
                edit.addQuestion();
                PluginData.getMessageUtil().sendInfoMessage(sender,"Changes saved. Do /game end when done.");
                break;
        }
    }

    private void sendCategories(ProxiedPlayer sender,QuizEditQuestion edit){
        sender.sendMessage(new ComponentBuilder("[Categories] "+edit.getCategories()).color(ChatColor.DARK_GREEN).create());
    }
}
