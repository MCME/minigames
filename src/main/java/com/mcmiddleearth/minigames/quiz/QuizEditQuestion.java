package com.mcmiddleearth.minigames.quiz;

import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.listener.quizListener.QuestionConversationType;
import com.mcmiddleearth.minigames.velocity.question.*;
import com.mcmiddleearth.minigames.util.PluginData;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jubo
 */
public class QuizEditQuestion {

    private static final List<Player> inEditConversation = new ArrayList<>();
    private static final HashMap<Player,QuizEditQuestion> editSave = new HashMap<>();
    private Integer index;
    private QuestionType type;
    private AbstractQuestion question;
    private final QuizGame game;
    private String aText = null;
    private String bText = null;
    private String cText = null;
    private String dText = null;
    private QuestionConversationType conType;

    public QuizEditQuestion(Player player,int index){
        game = (QuizGame) PluginData.getGame("review");
        if(game != null){
            this.index = index;
            conType = QuestionConversationType.QUESTION;
            question = game.getQuestions().get(index);
            type = question.getType();
            inEditConversation.add(player);
            editSave.put(player,this);
            if(question instanceof ChoiceQuestion){
                aText = ((ChoiceQuestion) question).getAnswers()[0];
                bText = ((ChoiceQuestion) question).getAnswers()[1];
                cText = ((ChoiceQuestion) question).getAnswers()[2];
                dText = ((ChoiceQuestion) question).getAnswers()[3];
            }
            //player.sendMessage(new ComponentBuilder("[Question] "+question.getQuestion()).color(ChatColor.DARK_GREEN).create());
        }
    }

    public static void cancel(Player player){
        inEditConversation.remove(player);
        editSave.remove(player);
    }

    public String getQuestion(){return question.getQuestion();}
    public void setQuestion(String questionText){question.setQuestion(questionText);}

    public String getCategories(){return question.getCategories();}
    public void setCategories(String categories){question.setCategories(categories);}

    public Integer getDeviation(){return ((NumberQuestion) question).getPrecision();}
    public void setDeviation(Integer deviation){((NumberQuestion) question).setPrecision(deviation);}

    public Integer getNumberAnswer(){return ((NumberQuestion)question).getAnswer();}
    public void setNumberAnswer(Integer answer){((NumberQuestion)question).setAnswer(answer);}

    public String getFreeAnswer(){return ((FreeQuestion)question).getAnswer();}
    public void setFreeAnswer(String answer){((FreeQuestion)question).setAnswer(answer);}

    public String getaAnswer(){return aText;}
    public void setaAnswer(String answer){aText = answer;}

    public String getbAnswer(){return bText;}
    public void setbAnswer(String answer){bText = answer;}

    public String getcAnswer(){return cText;}
    public void setcAnswer(String answer){cText = answer;}

    public String getdAnswer(){return dText;}
    public void setdAnswer(String answer){dText = answer;}

    private void setAnswers(){
        String[] choiceText = new String[]{aText,bText,cText,dText};
        ((ChoiceQuestion)question).setAnswers(choiceText);
    }

    public String getCorrectAnswer(){return ((ChoiceQuestion) question).getCorrectAnswer();}
    public void setCorrectAnswer(String answer){((ChoiceQuestion)question).setCorrectAnswers(answer);}

    public QuestionType getType(){return type;}

    public void addQuestion(){
        game.removeQuestion(index);
        if(question instanceof ChoiceQuestion)
            setAnswers();
        game.addQuestion(question,-1);
    }

    public void setConType(QuestionConversationType conType){this.conType = conType;}
    public QuestionConversationType getConType(){return conType;}

    public static boolean inConversation(Player player){
        return inEditConversation.contains(player);
    }

    public static QuizEditQuestion getEditInstance(Player player){
        if(editSave.containsKey(player))
            return editSave.get(player);
        return null;
    }
}
