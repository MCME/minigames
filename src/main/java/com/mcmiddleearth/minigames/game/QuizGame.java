package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.quiz.question.*;
import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import com.mcmiddleearth.minigames.scoreboard.QuizGameScoreboard;
import com.mcmiddleearth.minigames.util.NumericUtil;
import com.mcmiddleearth.minigames.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mcmiddleearth.minigames.quiz.question.QuestionType.getQuestionType;

/**
 * @author Jubo
 */
public class QuizGame extends AbstractGame{

    private final List<AbstractQuestion> questions = new ArrayList<>();

    private boolean randomQuestions = true;
    private boolean randomChoices = true;

    private int nextQuestion = 0;

    private int answerTime = 30;



    public QuizGame(ProxiedPlayer manager, String name) {
        super(manager, name,GameType.LORE_QUIZ,new QuizGameScoreboard());
    }

    @Override
    public void addPlayer(ProxiedPlayer player){
        super.addPlayer(player);
        ((QuizGameScoreboard) getBoard()).addPlayer(player);
    }

    @Override
    public String getGameChatTag(ProxiedPlayer player){
        if(getManager() == player)
            return ChatColor.DARK_AQUA + "<Host ";
        else
            return super.getGameChatTag(player);
    }

    public int[] loadQuestionsFromDataFile(File file, String quizCategories,
                                           boolean matchAllCategories, int maxNumber) throws FileNotFoundException {
        List<AbstractQuestion> newQuestions = new ArrayList<>();
        boolean loadAll = quizCategories.equalsIgnoreCase("all");
        int found=0;
        try {
            int line = 0;
            try (Scanner reader = new Scanner(file, StandardCharsets.UTF_8.name())) {
                while(reader.hasNext()){
                    try {
                        line++;
                        StringTokenizer tokenizer = new StringTokenizer(reader.nextLine(),";");
                        String questionCategories = tokenizer.nextToken();
                        if(loadAll || isQuestionInQuizCategories(quizCategories, questionCategories, matchAllCategories)) {
                            AbstractQuestion newQuestion = questionFromString(tokenizer, questionCategories);
                            newQuestion.setId(line);
                            newQuestions.add(newQuestion);
                        }
                    } catch (ParseException | NoSuchElementException ex) {
                        Logger.getLogger(QuizGame.class.getName()).log(Level.SEVERE,
                                "Error reading questions from data file in line "+line
                                        +". Question skipped. ");
                    }
                }
                found=newQuestions.size();
                while(newQuestions.size()>maxNumber) {
                    //int random = new Double(Math.floor(Math.random()*newQuestions.size())).intValue();
                    int random = NumericUtil.getRandom(0, newQuestions.size()-1);
                    if(random >= newQuestions.size()) {
                        random = newQuestions.size()-1;
                    }
                    newQuestions.remove(random);
                }
                for(AbstractQuestion question: newQuestions) {
                    addQuestion(question,-1);
                }
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
        return new int[]{found,newQuestions.size()};
    }

    public void addQuestion(AbstractQuestion question, int index) {
        if(index==-1) {
            questions.add(question);
        }
        else {
            questions.add(index, question);
            if(nextQuestion>=index) {
                nextQuestion++;
            }
        }
        ((QuizGameScoreboard)getBoard()).addQuestion();
    }

    private AbstractQuestion questionFromString(StringTokenizer tokenizer, String questionCategories) throws ParseException {
        QuestionType type = getQuestionType(StringUtil.parseInt(tokenizer.nextToken()));
        String question = tokenizer.nextToken();
        String[] choices = new String[]{"","","",""};
        if(type.equals(QuestionType.MULTI) || type.equals(QuestionType.SINGLE)) {
            for(int i = 0; i<4; i++) {
                choices[i] = tokenizer.nextToken();
            }
        }
        String answer = tokenizer.nextToken();
        AbstractQuestion newQuestion;
        switch(type) {
            case FREE:
                newQuestion = new FreeQuestion(question,answer,questionCategories);
                break;
            case NUMBER:
                int precision = StringUtil.parseInt(tokenizer.nextToken());
                int answerInt = StringUtil.parseInt(answer);
                newQuestion = new NumberQuestion(question,
                        answerInt,
                        precision,questionCategories);
                break;
            case MULTI:
                newQuestion = new ChoiceQuestion(question,
                        choices,
                        answer,questionCategories);
                break;
            case SINGLE:
                newQuestion = new SingleChoiceQuestion(question,
                        choices,
                        answer,questionCategories);
                break;
            default:
                throw new ParseException("Error",5);
        }
        return newQuestion;
    }

    private QuestionType getQuestionType(int index) {
        switch(index) {
            case 1: return QuestionType.FREE;
            case 2: return QuestionType.NUMBER;
            case 3: return QuestionType.SINGLE;
            case 4: return QuestionType.MULTI;
        }
        return QuestionType.FREE;
    }

    private boolean isQuestionInQuizCategories(String quizCategories,
                                               String questionCategories,
                                               boolean matchAllCategories) {
        String wantedCategories= "";
        String excludedCategories="";
        boolean exclude = false;
        for(char character: quizCategories.toCharArray()) {
            if(character=='-') {
                exclude = true;
            } else {
                if(exclude) {
                    excludedCategories+=character;
                } else {
                    wantedCategories+=character;
                }
                exclude = false;
            }
        }
        if(checkWantedCategories(wantedCategories, questionCategories, matchAllCategories)) {
            return checkExcludedCategories(excludedCategories, questionCategories);
        } else {
            return false;
        }
    }

    private boolean checkWantedCategories(String wantedQuizCategories,
                                          String questionCategories,
                                          boolean matchAllCategories) {
        for(char character: wantedQuizCategories.toCharArray()) {
            if(matchAllCategories) {
                if(questionCategories.indexOf(character)<0) {
                    return false;
                }
            } else {
                if(questionCategories.indexOf(character)>=0) {
                    return true;
                }
            }
        }
        return matchAllCategories;
    }

    private boolean checkExcludedCategories(String excludedCategories,
                                            String questionCategories) {
        for(char character: excludedCategories.toCharArray()) {
            if(questionCategories.indexOf(character)>=0) {
                return false;
            }
        }
        return true;
    }

}
