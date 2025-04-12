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
}
