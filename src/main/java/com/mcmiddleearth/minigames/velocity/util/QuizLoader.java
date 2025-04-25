package com.mcmiddleearth.minigames.velocity.util;

import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.runners.QuizRunner;
import com.mcmiddleearth.minigames.velocity.question.QuestionType;
import com.mcmiddleearth.minigames.velocity.question.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuizLoader {
    private static final File questionDir = new File(MiniGamesPlugin.getInstance().dataDirectory+File.separator+"QuizQuestions");

    static {
        if (!MiniGamesPlugin.getInstance().dataDirectory.toFile().exists()) {
            MiniGamesPlugin.getInstance().dataDirectory.toFile().mkdirs();
        }

        if (!questionDir.exists()) {
            questionDir.mkdirs();
        }
    }

    public static void loadQuiz(QuizRunner runner, String name) throws ParseException, FileNotFoundException {
        String input;
        try (Scanner reader = new Scanner(new File(questionDir + name + ".json"), StandardCharsets.UTF_8.name())) {
            input = "";
            while(reader.hasNext()){
                input = input+reader.nextLine();
            }
        }
        JSONObject jInput = (JSONObject) new JSONParser().parse(input);
        JSONArray jQuestions = (JSONArray) jInput.get("questions");
        for (Object questionObject : jQuestions) {
            JSONObject jQuestion = (JSONObject) questionObject;
            QuestionType type = QuestionType.getQuestionType((String) jQuestion.get("Type"));
            AbstractQuestion newQuestion;
            switch(type) {
                case FREE:
                    newQuestion = new FreeQuestion((String) jQuestion.get("Question"),
                            (String) jQuestion.get("Answer"),
                            (String) jQuestion.get("Categories"));
                    break;
                case NUMBER:
                    newQuestion = new NumberQuestion((String) jQuestion.get("Question"),
                            ((Long) jQuestion.get("Answer")).intValue(),
                            ((Long) jQuestion.get("Precision")).intValue(),
                            (String) jQuestion.get("Categories"));
                    break;
                case MULTI:
                    newQuestion = new ChoiceQuestion((String) jQuestion.get("Question"),
                            readStringArray(jQuestion,"Choices"),
                            (String) jQuestion.get("Correct"),
                            (String) jQuestion.get("Categories"));
                    break;
                case SINGLE:
                    newQuestion = new SingleChoiceQuestion((String) jQuestion.get("Question"),
                            readStringArray(jQuestion,"Choices"),
                            (String) jQuestion.get("Correct"),
                            (String) jQuestion.get("Categories"));
                    break;
                default:
                    throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
            }
            runner.allQuestions.add(newQuestion);
        }
    }

    private static String[] readStringArray(JSONObject jQuestion, String key) {
        JSONArray jAnswers = (JSONArray) jQuestion.get(key);
        List<String> answers= new ArrayList<>();
        for(Object answerObject : jAnswers) {
            answers.add((String) answerObject);
        }
        return answers.toArray(new String[0]);
    }

    public static void saveQuiz(QuizRunner runner, String name){

    }
}
