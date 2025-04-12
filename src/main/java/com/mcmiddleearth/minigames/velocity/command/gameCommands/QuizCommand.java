package com.mcmiddleearth.minigames.velocity.command.gameCommands;

import com.mcmiddleearth.minigames.velocity.command.ArgumentNames;
import com.mcmiddleearth.minigames.velocity.command.VelocitySuggester;
import com.mcmiddleearth.minigames.velocity.command.executor.QuizExecutor;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class QuizCommand {

    public static BrigadierCommand getQuizCommand(ProxyServer server){
        LiteralArgumentBuilder<CommandSource> game = BrigadierCommand.literalArgumentBuilder("quiz")
                .then(BrigadierCommand.literalArgumentBuilder("create")
                        .executes(QuizExecutor::CreateQuiz))
                .then(BrigadierCommand.literalArgumentBuilder("showcategories")
                        .executes(QuizExecutor::ShowCategories))
                .then(BrigadierCommand.literalArgumentBuilder("question")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.QUESTION_TYPE, StringArgumentType.word())
                                .suggests(VelocitySuggester::QuestionTypeArgument)
                                .executes(QuizExecutor::CreateQuestion)
                        )
                );
        return new BrigadierCommand(game
                .build());
    }
}
