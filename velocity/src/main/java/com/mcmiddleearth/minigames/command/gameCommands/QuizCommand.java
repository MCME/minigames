package com.mcmiddleearth.minigames.command.gameCommands;

import com.mcmiddleearth.minigames.command.ArgumentNames;
import com.mcmiddleearth.minigames.command.VelocitySuggester;
import com.mcmiddleearth.minigames.command.executor.QuizExecutor;
import com.mcmiddleearth.minigames.util.Permissions;
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
                .then(BrigadierCommand.literalArgumentBuilder("join")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.GAME_NAME, StringArgumentType.word())
                                .suggests(VelocitySuggester::ExistingQuizNameArgument)
                                .executes(QuizExecutor::JoinQuiz)))
                .then(BrigadierCommand.literalArgumentBuilder("leave")
                        .executes(QuizExecutor::LeaveQuiz));
        AddGameControls(game);
        AddGameSettings(game);
        AddFileLoading(game);
        return new BrigadierCommand(game.build());
    }

    private static void AddGameControls(LiteralArgumentBuilder<CommandSource> root){
        root.then(BrigadierCommand.literalArgumentBuilder("create")
                        .requires(Permissions.MANAGER::hasPermission)
                .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.GAME_NAME, StringArgumentType.word())
                    .executes(QuizExecutor::CreateQuiz)))
            .then(BrigadierCommand.literalArgumentBuilder("clear")
                    .executes(QuizExecutor::ClearQuestions))
            .then(BrigadierCommand.literalArgumentBuilder("winner")
                    .executes(QuizExecutor::AnnounceWinners))
            .then(BrigadierCommand.literalArgumentBuilder("restart")
                    .executes(QuizExecutor::RestartQuiz))
            .then(BrigadierCommand.literalArgumentBuilder("start")
                    .executes(QuizExecutor::StartQuiz))
            .then(BrigadierCommand.literalArgumentBuilder("send")
                    .executes(QuizExecutor::SendQuestion)
                    .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.TIME_LIMIT, IntegerArgumentType.integer(1))
                            .suggests(VelocitySuggester::TimeLimitArgument)
                            .executes(QuizExecutor::SendQuestion)));
    }
    private static void AddGameSettings(LiteralArgumentBuilder<CommandSource> root){
        root.then(BrigadierCommand.literalArgumentBuilder("random")
                .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.QUESTION_RANDOM_TYPES, StringArgumentType.string())
                        .suggests(VelocitySuggester::QuestionRandomTYpeArgument)
                        .executes(QuizExecutor::SetRandomness)))
            .then(BrigadierCommand.literalArgumentBuilder("setanswertime")
                    .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.TIME_LIMIT, IntegerArgumentType.integer(1))
                            .suggests(VelocitySuggester::TimeLimitArgument)
                            .executes(QuizExecutor::SetTimeLimit)));
    }

    private static void AddFileLoading(LiteralArgumentBuilder<CommandSource> root){
        root.then(BrigadierCommand.literalArgumentBuilder("savequiz")
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
                                        .executes(QuizExecutor::LoadQuestions)))));
    }
}
