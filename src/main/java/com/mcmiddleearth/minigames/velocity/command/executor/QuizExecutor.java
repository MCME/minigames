package com.mcmiddleearth.minigames.velocity.command.executor;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;

public class QuizExecutor {
    public static int ShowCategories(CommandContext<CommandSource> commandSourceCommandContext) {

        return Command.SINGLE_SUCCESS;
    }
}
