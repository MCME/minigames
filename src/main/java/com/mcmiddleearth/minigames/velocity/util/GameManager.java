package com.mcmiddleearth.minigames.velocity.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.velocity.runners.GameRunner;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.HashMap;
import java.util.Map;

import com.mcmiddleearth.minigames.common.Channels;

public class GameManager {

    public final HashMap<String, GameRunner> proxyGames = new HashMap<>();
    public final Map<String, Player> backendGames = new HashMap<>();

    public void createProxyGame(String gameName, String gameType){

    }

    public void createBackendGame(String gameName, String gameType){

    }

    public void joinGame(Player player, String gameName){
        GameRunner proxyRunner = proxyGames.get(gameName);
        if(proxyRunner != null){
            proxyRunner.join(player);
            return;
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Join");
        out.writeUTF(gameName);
        out.writeUTF(player.getUsername());

        RegisteredServer backend = backendGames.get(gameName).getCurrentServer().orElseThrow().getServer();
        backend.sendPluginMessage(Channels.GAMEMANAGER, out.toByteArray());
        player.createConnectionRequest(backend);

    }
}
