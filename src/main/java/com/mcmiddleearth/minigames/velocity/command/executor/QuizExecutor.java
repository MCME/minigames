package com.mcmiddleearth.minigames.velocity.command.executor;

import com.mcmiddleearth.minigames.spigot.util.MessageUtil;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.command.ArgumentNames;
import com.mcmiddleearth.minigames.velocity.runners.QuizRunner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

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

    public static int CreateQuestion(CommandContext<CommandSource> c) {

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

        return Command.SINGLE_SUCCESS;
    }

    public static int LoadQuiz(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int LoadQuestions(CommandContext<CommandSource> c) {
        //TODO: branching path with 2 optional arguments

        return Command.SINGLE_SUCCESS;
    }

    public static int ClearQuestions(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int SetRandomness(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int AnnounceWinners(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int RestartQuiz(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }
}
