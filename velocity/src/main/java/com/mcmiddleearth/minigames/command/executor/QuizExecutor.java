package com.mcmiddleearth.minigames.command.executor;

import com.google.gson.JsonSyntaxException;
import com.mcmiddleearth.minigames.runners.GameRunner;
import com.mcmiddleearth.minigames.util.MessageUtil;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.command.ArgumentNames;
import com.mcmiddleearth.minigames.runners.quiz.QuizRunner;
import com.mcmiddleearth.minigames.runners.quiz.QuizRandomness;
import com.mcmiddleearth.minigames.util.QuestionLoader;
import com.mcmiddleearth.minigames.util.QuizLoader;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.NotImplementedException;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Optional;

public class QuizExecutor {
    public static int CreateQuiz(CommandContext<CommandSource> c) {
        String quizName = c.getArgument(ArgumentNames.GAME_NAME, String.class);
        Player manager = (Player) c.getSource();

        QuizRunner runner = new QuizRunner(quizName, manager);
        MiniGamesPlugin.proxyGames.put(quizName, runner);
        MessageUtil.sendInfoMessage(manager, "Quiz created!");

        Audience.audience(MiniGamesPlugin.getInstance().server.getAllPlayers()).sendMessage(
                Component.text(manager.getUsername() + " started a quiz do '/quiz join " + quizName + "' to join!")
                        .color(NamedTextColor.AQUA));
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
        runner.sendQuestion(Optional.ofNullable(c.getArguments().get(ArgumentNames.TIME_LIMIT))
                .map(arg -> (Integer) arg.getResult())
                .orElse(runner.ANSWER_TIME_SEC));

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

        try {
            QuizLoader.loadQuiz(optionalQuizRunner.get(), name);
        } catch(JsonSyntaxException | FileNotFoundException exception){
            MessageUtil.sendErrorMessage(manager, "Quiz failed to load, send in dev-public to check the logs.");
            MiniGamesPlugin.getInstance().getLogger().error("Quiz failed to load: {}", name, exception);
            return Command.SINGLE_SUCCESS;
        }
        MessageUtil.sendInfoMessage(manager, "Loaded quiz, ready to start.");

        return Command.SINGLE_SUCCESS;
    }

    public static int LoadQuestions(CommandContext<CommandSource> c) {
        Player manager = (Player) c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if (optionalQuizRunner.isEmpty()) {
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }

        QuizRunner runner = optionalQuizRunner.get();
        Map<String, ParsedArgument<CommandSource, ?>> arguments = c.getArguments();
        String categories = Optional.ofNullable(arguments.get(ArgumentNames.CATEGORIES))
                .map(arg -> (String) arg.getResult())
                .orElse(null);
        Boolean matchAll = Optional.ofNullable(arguments.get(ArgumentNames.MATCH_ALL))
                .map(arg -> (Boolean) arg.getResult())
                .orElse(false);
        Integer questionAmount = Optional.ofNullable(arguments.get(ArgumentNames.QUESTION_AMOUNT))
                .map(arg -> (Integer) arg.getResult())
                .orElse(15);
        if (categories == null){
            MessageUtil.sendErrorMessage(manager, "Something went wrong with the categories, did you leave this empty? If not report this in dev-public!");
            return Command.SINGLE_SUCCESS;
        }
        QuestionLoader.loadQuestions(runner, categories, matchAll, questionAmount);
        MessageUtil.sendInfoMessage(manager, "Questions loaded!");
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
        Player manager = (Player)c.getSource();
        Optional<QuizRunner> optionalQuizRunner = MiniGamesPlugin.proxyGames.values().stream()
                .filter(gameRunner -> gameRunner.getManager().equals(manager) && gameRunner instanceof QuizRunner)
                .findFirst().map(gameRunner -> (QuizRunner) gameRunner);
        if(optionalQuizRunner.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "You are not a manager of a quiz game.");
            return Command.SINGLE_SUCCESS;
        }
        optionalQuizRunner.get().announceWinners();

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

    public static int JoinQuiz(CommandContext<CommandSource> c) {
        Player joiner = (Player)c.getSource();
        String gameName = c.getArgument(ArgumentNames.GAME_NAME, String.class);
        GameRunner game = MiniGamesPlugin.proxyGames.get(gameName);
        if(game == null)
            return BrigadierCommand.FORWARD;
        if(!(game instanceof QuizRunner)){
            MessageUtil.sendErrorMessage(joiner, "Can't join the game, it does not exist. If it did not end, report this in dev-public.");
            return Command.SINGLE_SUCCESS;
        }
        if(game.canJoin(joiner)) {
            game.join(joiner);
            return Command.SINGLE_SUCCESS;
        }
        MessageUtil.sendInfoMessage(joiner, "You can't join the game.");
        return Command.SINGLE_SUCCESS;
    }

    public static int LeaveQuiz(CommandContext<CommandSource> c) {
        Player leaver = (Player) c.getSource();
        MiniGamesPlugin.proxyGames.values().stream().filter(game -> game.getPlayers().contains(leaver))
                .forEach(game -> game.leave(leaver, false));
        return Command.SINGLE_SUCCESS;
    }
}
