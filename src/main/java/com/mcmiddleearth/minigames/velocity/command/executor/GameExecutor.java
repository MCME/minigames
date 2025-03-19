package com.mcmiddleearth.minigames.velocity.command.executor;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

public class GameExecutor {

    public static int Check(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int JoinGame(CommandContext<CommandSource> c) {

        return BrigadierCommand.FORWARD;
    }

    public static int LeaveGame(CommandContext<CommandSource> c) {

        return BrigadierCommand.FORWARD;
    }

    public static int SendStats(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int SendHelp(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int CreateGame(CommandContext<CommandSource> c) {

        return BrigadierCommand.FORWARD;
    }
}
