package com.mcmiddleearth.minigames.velocity.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.velocity.runners.GameRunner;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.HashMap;
import java.util.Map;

public class GameManager {
    public static final String GAMEMANAGER_PAPER = "minigames:gamemanager";
    public static final MinecraftChannelIdentifier GAMEMANAGER = MinecraftChannelIdentifier.from(GAMEMANAGER_PAPER);

    private final HashMap<String, GameRunner> proxyGames = new HashMap<>();
    private final Map<String, RegisteredServer> backendGames = new HashMap<>();

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

        RegisteredServer backend = backendGames.get(gameName);
        backend.sendPluginMessage(GAMEMANAGER, out.toByteArray());
        player.createConnectionRequest(backend);

    }

    public Map<String, GameRunner> getProxyGames(){return proxyGames;}
    public Map<String, RegisteredServer> getBackendGames(){return backendGames;}
}
