package com.mcmiddleearth.minigames.quiz;

import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.listener.quizListener.QuestionConversationType;
import com.mcmiddleearth.minigames.velocity.question.*;
import com.mcmiddleearth.minigames.spigot.util.PluginData;
import com.velocitypowered.api.proxy.Player;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jubo
 */
public class QuizSubmitQuestion {

    private static final List<Player> inSubmitConversation = new ArrayList<>();
    private static final HashMap<Player,QuizSubmitQuestion> submitSave = new HashMap<>();
    private final QuestionType type;
    private String questionText = null;
    private String aText = null;
    private String bText = null;
    private String cText = null;
    private String dText = null;
    private String correctAnswer = null;
    private String freeText = null;
    private Integer numberText = null;
    private Integer deviation = null;
    private String categories = null;
    private final QuizGame game;
    private QuestionConversationType conType;

    public QuizSubmitQuestion(Player player,QuestionType type){
        inSubmitConversation.add(player);
        submitSave.put(player,this);
        this.type = type;
        game = new QuizGame(null,"submitQuestions");
        conType = QuestionConversationType.QUESTION;
    }

    public static void cancel(Player player){
        inSubmitConversation.remove(player);
        submitSave.remove(player);
    }

    public QuestionType getType(){return type;}

    public void setQuestionText(String text){questionText = text;}

    public void setaText(String text){aText = text;}

    public void setbText(String text){bText = text;}

    public void setcText(String text){cText = text;}

    public void setdText(String text){dText = text;}

    public void setCorrectAnswer(String correctAnswer){
        this.correctAnswer = correctAnswer;
    }

    public void setFreeText(String text){
        freeText = text;
    }

    public void setNumberText(Integer numberText){this.numberText = numberText;}

    public void setDeviation(Integer deviation){
        this.deviation = deviation;
    }

    public String getCategories(){return categories;}
    public void setCategories(String categories){this.categories = categories;}

    public void saveQuestion(){
        try {
            game.loadQuestionsFromJson(PluginData.getSubmittedQuestionsFile());
            (PluginData.getSubmittedQuestionsFile()).delete();
        } catch (FileNotFoundException | ParseException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.INFO, "No submitted questions found.");
        }
        AbstractQuestion question = null;
        String[] choiceText = new String[]{aText,bText,cText,dText};
        question = switch (type) {
            case FREE -> new FreeQuestion(questionText, freeText, categories);
            case MULTI -> new ChoiceQuestion(questionText, choiceText, correctAnswer, categories);
            case SINGLE -> new SingleChoiceQuestion(questionText, choiceText, correctAnswer, categories);
            case NUMBER -> new NumberQuestion(questionText, numberText, deviation, categories);
        };
        game.addQuestion(question,-1);
        try{
            game.saveQuestionsToJson(PluginData.getSubmittedQuestionsFile(),"Submitted quiz questions.");
        }catch (IOException ex) {
            Logger.getLogger(QuizSubmitQuestion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setConType(QuestionConversationType conType){this.conType = conType;}
    public QuestionConversationType getConType(){return conType;}

    public static boolean inConversation(Player player){
        return inSubmitConversation.contains(player);
    }

    public static QuizSubmitQuestion getSubmitInstance(Player player){
        if(submitSave.containsKey(player))
            return submitSave.get(player);
        return null;
    }
}
