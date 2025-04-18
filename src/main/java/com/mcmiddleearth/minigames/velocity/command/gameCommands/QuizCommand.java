package com.mcmiddleearth.minigames.velocity.command.gameCommands;

import com.mcmiddleearth.minigames.velocity.command.ArgumentNames;
import com.mcmiddleearth.minigames.velocity.command.VelocitySuggester;
import com.mcmiddleearth.minigames.velocity.command.executor.QuizExecutor;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class QuizCommand {

    public static BrigadierCommand getQuizCommand(ProxyServer server){
        LiteralArgumentBuilder<CommandSource> game = BrigadierCommand.literalArgumentBuilder("quiz")
                .then(BrigadierCommand.literalArgumentBuilder("create")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.GAME_NAME, StringArgumentType.word())
                            .executes(QuizExecutor::CreateQuiz)))
                .then(BrigadierCommand.literalArgumentBuilder("showcategories")
                        .executes(QuizExecutor::ShowCategories))
                //TODO: Add question branch
                .then(BrigadierCommand.literalArgumentBuilder("send")
                        .executes(QuizExecutor::SendQuestion)
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.TIME_LIMIT, IntegerArgumentType.integer(1))
                                .suggests(VelocitySuggester::TimeLimitArgument)
                                .executes(QuizExecutor::SendQuestion)))
                .then(BrigadierCommand.literalArgumentBuilder("stat")
                        .executes(QuizExecutor::SendStats))
                .then(BrigadierCommand.literalArgumentBuilder("savequiz")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.FILE_NAME, StringArgumentType.word())
                                .suggests(VelocitySuggester::NewFileNameArgument)
                                .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.DESCRIPTION, StringArgumentType.greedyString())
                                        .suggests(VelocitySuggester::DescriptionArgument)
                                        .executes(QuizExecutor::SaveQuiz))))
                .then(BrigadierCommand.literalArgumentBuilder("loadquiz")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.FILE_NAME, StringArgumentType.word())
                                .suggests(VelocitySuggester::ExistingQuizFileArgument)
                                .executes(QuizExecutor::LoadQuiz)))
                .then(BrigadierCommand.literalArgumentBuilder("loadquestions")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.CATEGORIES, StringArgumentType.word())
                                .suggests(VelocitySuggester::CategoryArgument)
                                .executes(QuizExecutor::LoadQuestions)
                                .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.MATCH_ALL, BoolArgumentType.bool())
                                        .executes(QuizExecutor::LoadQuestions)
                                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.QUESTION_AMOUNT, IntegerArgumentType.integer(1))
                                                .executes(QuizExecutor::LoadQuestions)))))
                .then(BrigadierCommand.literalArgumentBuilder("clear")
                        .executes(QuizExecutor::ClearQuestions))
                .then(BrigadierCommand.literalArgumentBuilder("random")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.QUESTION_RANDOM_TYPES, StringArgumentType.string())
                                .suggests(VelocitySuggester::QuestionRandomTYpeArgument)
                                .executes(QuizExecutor::SetRandomness)))
                .then(BrigadierCommand.literalArgumentBuilder("winner")
                        .executes(QuizExecutor::AnnounceWinners))
                .then(BrigadierCommand.literalArgumentBuilder("restart")
                        .executes(QuizExecutor::RestartQuiz))
                ;
        return new BrigadierCommand(game
                .build());
    }
}
