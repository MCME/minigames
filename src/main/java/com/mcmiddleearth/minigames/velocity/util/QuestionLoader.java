package com.mcmiddleearth.minigames.velocity.util;

import com.mcmiddleearth.minigames.spigot.util.NumericUtil;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.question.AbstractQuestion;
import com.mcmiddleearth.minigames.velocity.question.QuestionParser;
import com.mcmiddleearth.minigames.velocity.runners.QuizRunner;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class QuestionLoader {
    private static final File questionDir = new File(MiniGamesPlugin.getInstance().dataDirectory+File.separator+"QuizQuestions");
    private static final File questionDataTable = new File(questionDir,"questionTable.dat");
    private static final File questionCategoriesFile = new File(questionDir,"questionCategories.dat");
    private static final List<String> questionCategories = new ArrayList<>();

    static{
        if(!MiniGamesPlugin.getInstance().dataDirectory.toFile().exists()){
            MiniGamesPlugin.getInstance().dataDirectory.toFile().mkdirs();
        }

        if(!questionDir.exists()) {
            questionDir.mkdirs();
        }

        try {
            try (Scanner reader = new Scanner(questionCategoriesFile, StandardCharsets.UTF_8.name())) {
                questionCategories.clear();
                if(reader.hasNext()) {
                    reader.nextLine();
                }
                while(reader.hasNext()){
                    questionCategories.add(reader.nextLine());
                }
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getInstance().getLogger().error(null, ex);
        }
    }
    public static void loadQuestions(QuizRunner runner, @NotNull String categories, Boolean matchAll, Integer amount) {
        List<AbstractQuestion> newQuestions = new ArrayList<>();
        int categorySplitLocation = categories.indexOf('-') == -1 ? categories.length() : categories.indexOf('-');
        String wantedCategories = categories.substring(0, categorySplitLocation);
        String unwantedCategories;
        if (categorySplitLocation == categories.length())
            unwantedCategories = "";
        else
            unwantedCategories = categories.substring(categorySplitLocation + 1);
        try {
            int line = 0;
            try (Scanner reader = new Scanner(questionDataTable, StandardCharsets.UTF_8.name())) {
                while (reader.hasNext()) {
                    try {
                        line++;
                        String question = reader.nextLine();
                        StringTokenizer tokenizer = new StringTokenizer(question, ";");
                        String questionCategories = tokenizer.nextToken();
                        if (categories.equals("all") || (
                                questionCategories.matches(String.format("([^%s]{%d})", unwantedCategories, questionCategories.length())) &&
                                (matchAll ?
                                        Arrays.stream(wantedCategories.split("")).allMatch(questionCategories::contains) :
                                        questionCategories.matches(String.format(".*[%s].*", wantedCategories))))) {
                            AbstractQuestion newQuestion = QuestionParser.parseQuestionFromString(question);
                            newQuestion.setId(line);
                            newQuestions.add(newQuestion);
                        }
                    } catch (ParseException | NoSuchElementException ex) {
                        MiniGamesPlugin.getInstance().logger.error("Error reading questions from data file in line {}. Question skipped.", line);
                    }
                }
                while (newQuestions.size() > amount) {
                    int random = NumericUtil.getRandom(0, newQuestions.size() - 1);
                    if (random >= newQuestions.size()) {
                        random = newQuestions.size() - 1;
                    }
                    newQuestions.remove(random);
                }
                runner.allQuestions.addAll(newQuestions);
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getInstance().logger.error(null, ex);
            throw ex;
        }

    }

    public static void saveQuestions(QuizRunner runner, String name){

    }
}
