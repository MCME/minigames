package com.mcmiddleearth.minigames.velocity.command.executor;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;

public class QuizExecutor {
    public static int CreateQuiz(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int ShowCategories(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int CreateQuestion(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int SendQuestion(CommandContext<CommandSource> c) {
        //TODO: branching path one with possible argument, other without

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
