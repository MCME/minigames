package com.mcmiddleearth.minigames.util;

import com.google.gson.*;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.runners.quiz.QuizRunner;
import com.mcmiddleearth.minigames.question.QuestionType;
import com.mcmiddleearth.minigames.question.*;

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

    public static void loadQuiz(QuizRunner runner, String name) throws JsonSyntaxException, FileNotFoundException {
        String input;
        try (Scanner reader = new Scanner(new File(questionDir + name + ".json"), StandardCharsets.UTF_8.name())) {
            input = "";
            while(reader.hasNext()){
                input = input+reader.nextLine();
            }
        }
        Gson gson = new Gson();
        JsonObject jInput = gson.fromJson(input, JsonObject.class);
        JsonArray jQuestions = jInput.getAsJsonArray("questions");
        for (JsonElement questionElement : jQuestions) {
            JsonObject jQuestion = questionElement.getAsJsonObject();
            QuestionType type = QuestionType.getQuestionType(jQuestion.get("Type").getAsString());
            AbstractQuestion newQuestion = switch (type) {
                case FREE -> new FreeQuestion(jQuestion.get("Question").getAsString(),
                        jQuestion.get("Answer").getAsString(),
                        jQuestion.get("Categories").getAsString());
                case NUMBER -> new NumberQuestion(jQuestion.get("Question").getAsString(),
                        jQuestion.get("Answer").getAsInt(),
                        jQuestion.get("Precision").getAsInt(),
                        jQuestion.get("Categories").getAsString());
                case MULTI -> new ChoiceQuestion(jQuestion.get("Question").getAsString(),
                        readStringArray(jQuestion, "Choices"),
                        jQuestion.get("Correct").getAsString(),
                        jQuestion.get("Categories").getAsString());
                case SINGLE -> new SingleChoiceQuestion(jQuestion.get("Question").getAsString(),
                        readStringArray(jQuestion, "Choices"),
                        jQuestion.get("Correct").getAsString(),
                        jQuestion.get("Categories").getAsString());
                default -> throw new JsonSyntaxException("Unexpected question type");
            };
            runner.allQuestions.add(newQuestion);
        }
    }

    private static String[] readStringArray(JsonObject jQuestion, String key) {
        JsonArray jAnswers = jQuestion.getAsJsonArray(key);
        List<String> answers = new ArrayList<>();
        for (JsonElement answerElement : jAnswers) {
            answers.add(answerElement.getAsString());
        }
        return answers.toArray(new String[0]);
    }

    public static void saveQuiz(QuizRunner runner, String name){

    }
}
