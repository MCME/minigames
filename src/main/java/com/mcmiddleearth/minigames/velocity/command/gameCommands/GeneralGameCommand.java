package com.mcmiddleearth.minigames.velocity.command.gameCommands;

import com.mcmiddleearth.minigames.velocity.command.ArgumentNames;
import com.mcmiddleearth.minigames.velocity.command.VelocitySuggester;
import com.mcmiddleearth.minigames.velocity.command.executor.GameExecutor;
import com.mcmiddleearth.minigames.velocity.command.executor.QuizExecutor;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class GeneralGameCommand {

    public static BrigadierCommand GameCommand(ProxyServer server){
        LiteralArgumentBuilder<CommandSource> game = BrigadierCommand.literalArgumentBuilder("game")
                .then(BrigadierCommand.literalArgumentBuilder("check")
                        .executes(GameExecutor::Check))
                .then(BrigadierCommand.literalArgumentBuilder("join")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.GAME_NAME, StringArgumentType.word())
                            .executes(GameExecutor::JoinGame)))
                .then(BrigadierCommand.literalArgumentBuilder("leave")
                        .executes(GameExecutor::LeaveGame))
                .then(BrigadierCommand.literalArgumentBuilder("stats")
                        .executes(GameExecutor::SendStats))
                .then(BrigadierCommand.literalArgumentBuilder("help")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.HELP_TYPE, StringArgumentType.word())
                                .suggests(VelocitySuggester::HelpArgument)
                                .executes(GameExecutor::SendHelp)))
                .then(BrigadierCommand.literalArgumentBuilder("create")
                        .then(BrigadierCommand.requiredArgumentBuilder(ArgumentNames.GAME_TYPE, StringArgumentType.word())
                                .suggests(VelocitySuggester::GameTypeArgument)
                                .executes(GameExecutor::CreateGame)
                        )
                );
        return new BrigadierCommand(game
                .build());
    }
}
