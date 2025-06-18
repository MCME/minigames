package com.mcmiddleearth.minigames.command.executor;

import com.mcmiddleearth.minigames.command.ArgumentNames;
import com.mcmiddleearth.minigames.runners.GameRunner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import static com.mcmiddleearth.minigames.MiniGamesPlugin.proxyGames;

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
