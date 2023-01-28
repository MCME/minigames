package com.mcmiddleearth.minigames.quiz;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quiz.question.*;
import com.mcmiddleearth.minigames.util.PluginData;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.checkerframework.checker.units.qual.A;
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

    private static final List<ProxiedPlayer> inSubmitConversation = new ArrayList<>();
    private static final HashMap<ProxiedPlayer,QuizSubmitQuestion> submitSave = new HashMap<>();
    private boolean answeresSet = false;
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

    public QuizSubmitQuestion(ProxiedPlayer player,QuestionType type){
        inSubmitConversation.add(player);
        submitSave.put(player,this);
        this.type = type;
        game = new QuizGame(null,"submitQuestions");
    }

    public static void cancel(ProxiedPlayer player){
        inSubmitConversation.remove(player);
        submitSave.remove(player);
    }

    public QuestionType getType(){return type;}

    public String getQuestionText(){return questionText;}
    public void setQuestionText(String text){questionText = text;}

    public String getaText(){return aText;}
    public void setaText(String text){aText = text;}

    public String getbText(){return bText;}
    public void setbText(String text){bText = text;}

    public String getcText(){return cText;}
    public void setcText(String text){cText = text;}

    public String getdText(){return dText;}
    public void setdText(String text){dText = text;}

    public String getCorrectAnswer(){return correctAnswer;}
    public void setCorrectAnswer(String correctAnswer){
        this.correctAnswer = correctAnswer;
        answeresSet = true;
    }

    public String getFreeText(){return freeText;}
    public void setFreeText(String text){
        freeText = text;
        answeresSet = true;
    }

    public Integer getNumberText(){return numberText;}
    public void setNumberText(Integer numberText){this.numberText = numberText;}

    public Integer getDeviation(){return deviation;}
    public void setDeviation(Integer deviation){
        this.deviation = deviation;
        answeresSet = true;
    }

    public String getCategories(){return categories;}
    public void setCategories(String categories){this.categories = categories;}

    public boolean answeresSet(){return answeresSet;}

    public void saveQuestion(){
        try {
            game.loadQuestionsFromJson(PluginData.getSubmittedQuestionsFile());
            (PluginData.getSubmittedQuestionsFile()).delete();
        } catch (FileNotFoundException | ParseException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.INFO, "No submitted questions found.");
        }
        AbstractQuestion question = null;
        String[] choiceText = new String[]{aText,bText,cText,dText};
        switch (type){
            case FREE:
                question = new FreeQuestion(questionText,freeText,categories);
                break;
            case MULTI:
                question = new ChoiceQuestion(questionText,choiceText,correctAnswer,categories);
                break;
            case SINGLE:
                question = new SingleChoiceQuestion(questionText,choiceText,correctAnswer,categories);
                break;
            case NUMBER:
                question = new NumberQuestion(questionText,numberText,deviation,categories);
                break;
        }
        game.addQuestion(question,-1);
        try{
            game.saveQuestionsToJson(PluginData.getSubmittedQuestionsFile(),"Submitted quiz questions.");
        }catch (IOException ex) {
            Logger.getLogger(QuizSubmitQuestion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean inConversation(ProxiedPlayer player){
        return inSubmitConversation.contains(player);
    }

    public static QuizSubmitQuestion getSubmitInstance(ProxiedPlayer player){
        if(submitSave.containsKey(player))
            return submitSave.get(player);
        return null;
    }


}
