package com.mcmiddleearth.minigames.velocity.command.executor;

import com.mcmiddleearth.minigames.spigot.util.MessageUtil;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.command.ArgumentNames;
import com.mcmiddleearth.minigames.velocity.runners.QuizRunner;
import com.mcmiddleearth.minigames.velocity.runners.util.QuizRandomness;
import com.mcmiddleearth.minigames.velocity.util.QuizLoader;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;

public class QuizExecutor {
    public static int CreateQuiz(CommandContext<CommandSource> c) {
        String quizName = c.getArgument(ArgumentNames.GAME_NAME, String.class);
        Player manager = (Player) c.getSource();

        QuizRunner runner = new QuizRunner(quizName, manager);
        MiniGamesPlugin.proxyGames.put(quizName, runner);
        MessageUtil.sendInfoMessage(manager, "Quiz created!");

        return Command.SINGLE_SUCCESS;
    }

    public static int ShowCategories(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int SendQuestion(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        QuizRunner runner = optionalQuizRunner.get();
        try {
            int timeLimit = c.getArgument(ArgumentNames.TIME_LIMIT, Integer.class);
            runner.sendQuestion(timeLimit);
        } catch(IllegalArgumentException e){
            if(e.getMessage().startsWith("No such argument"))
                runner.sendQuestion();
            else
                MessageUtil.sendErrorMessage(manager, "Something went wrong, send in dev-public: 'SendQuestion" +
                        " casts to the wrong class in the minigames plugin.'");
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int SendStats(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int SaveQuiz(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        String name = c.getArgument(ArgumentNames.FILE_NAME, String.class);
        QuizLoader.saveQuiz(optionalQuizRunner.get(), name);
        MessageUtil.sendInfoMessage(manager, "Saved quiz.");

        return Command.SINGLE_SUCCESS;
    }

    public static int LoadQuiz(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        String name = c.getArgument(ArgumentNames.FILE_NAME, String.class);
        QuizLoader.loadQuiz(optionalQuizRunner.get(), name);
        MessageUtil.sendInfoMessage(manager, "Loaded quiz, ready to start.");

        return Command.SINGLE_SUCCESS;
    }

    public static int LoadQuestions(CommandContext<CommandSource> c) {
        //TODO: branching path with 2 optional arguments

        return Command.SINGLE_SUCCESS;
    }

    public static int ClearQuestions(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }

        optionalQuizRunner.get().clearQuestions();
        MessageUtil.sendInfoMessage(manager, "Cleared the questions of this quiz.");

        return Command.SINGLE_SUCCESS;
    }

    public static int SetRandomness(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        QuizRunner runner = optionalQuizRunner.get();
        try {
            runner.randomness = QuizRandomness.getQuizRandomness(c.getArgument(ArgumentNames.QUESTION_RANDOM_TYPES, String.class));
            MessageUtil.sendInfoMessage(manager, "Randomness for your quiz set to " + runner.randomness);
        } catch(IllegalArgumentException e){
            if(e.getMessage().startsWith("No such argument"))
                runner.sendQuestion();
            else
                MessageUtil.sendErrorMessage(manager, "Something went wrong, send in dev-public: 'whilst getting" +
                        " randomness the cast is wrong, or a wrong value is passed to the enum QuizRandomness in the minigames plugin.'");
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int AnnounceWinners(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int RestartQuiz(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        optionalQuizRunner.get().restart();

        return Command.SINGLE_SUCCESS;
    }

    public static int StartQuiz(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        optionalQuizRunner.get().restart();

        MessageUtil.sendInfoMessage(manager, "Quiz has been started, you can alter the default answering time (30 seconds)" +
                " through the `/quiz setanswertime [time in seconds]` command or send a question through the `/quiz send <time in seconds>`" +
                " where the time is optional and if set, only works for ONE question.");
        return Command.SINGLE_SUCCESS;
    }

    public static int SetTimeLimit(CommandContext<CommandSource> c) {
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        QuizRunner runner = optionalQuizRunner.get();
        try {
            int timeLimit = c.getArgument(ArgumentNames.TIME_LIMIT, Integer.class);
            runner.ANSWER_TIME_SEC = timeLimit;
            MessageUtil.sendInfoMessage(manager, "Answer time set to: " + timeLimit);
        } catch(IllegalArgumentException e){
                MessageUtil.sendErrorMessage(manager, "Something went wrong, send in dev-public: 'SendQuestion" +
                        " casts to the wrong class in the minigames plugin.'");
        }

        return Command.SINGLE_SUCCESS;
    }
}
