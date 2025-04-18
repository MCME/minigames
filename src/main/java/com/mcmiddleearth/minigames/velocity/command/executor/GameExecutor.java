package com.mcmiddleearth.minigames.velocity.command.executor;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.common.Channels;
import com.mcmiddleearth.minigames.velocity.command.ArgumentNames;
import com.mcmiddleearth.minigames.velocity.runners.GameRunner;
import com.mcmiddleearth.minigames.velocity.util.BackendGame;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import static com.mcmiddleearth.minigames.velocity.MiniGamesPlugin.backendGames;
import static com.mcmiddleearth.minigames.velocity.MiniGamesPlugin.proxyGames;

public class GameExecutor {

    public static int Check(CommandContext<CommandSource> c) {

        return Command.SINGLE_SUCCESS;
    }

    public static int JoinGame(CommandContext<CommandSource> c) {
        String gameName = c.getArgument(ArgumentNames.GAME_NAME, String.class);
        Player player = (Player) c.getSource();
        GameRunner proxyRunner = proxyGames.get(gameName);
        if(proxyRunner != null){
            proxyRunner.join(player);
            return Command.SINGLE_SUCCESS;
        }

//        BackendGame game= backendGames.get(gameName);
//        if(game == null || game.manager == null || game.manager.getCurrentServer().isEmpty()) {
//            //TODO: Send nope
//            ;
//            return Command.SINGLE_SUCCESS;
//        }
//        RegisteredServer backend = game.manager.getCurrentServer().get().getServer();
//        ByteArrayDataOutput out = ByteStreams.newDataOutput();
//        out.writeUTF("Join");
//        out.writeUTF(gameName);
//        out.writeUTF(player.getUsername());
//
//        backend.sendPluginMessage(Channels.GAMEMANAGER, out.toByteArray());
//        player.createConnectionRequest(backend);

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
