package com.mcmiddleearth.minigames.velocity.scoreboard.generics;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.util.*;

/**
 * @author Jubo, NicovicTheSixth
 */

public abstract class AbstractGameScoreboard {
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("minigames:scoreboard");
    protected enum CAR {CREATE, ADD, REMOVE}

    protected final String name;
    protected final List<Player> players = new ArrayList<>();

    public AbstractGameScoreboard(String name, Player creator){
        this.name = name;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(CAR.CREATE.ordinal());
        creator.getCurrentServer().ifPresent(connection -> connection.sendPluginMessage(IDENTIFIER,out.toByteArray()));
    }

    public void removePlayer(Player player){
        players.remove(player);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(CAR.REMOVE.ordinal());
        out.writeUTF(player.getUsername());
        player.getCurrentServer().ifPresent(connection -> connection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
    }

    public void addPlayer(Player player){
        players.add(player);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(CAR.ADD.ordinal());
        out.writeUTF(name);
        out.writeUTF(player.getUsername());
        player.getCurrentServer().ifPresent(connection -> connection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
    }

    protected void createObjective(ScoreboardObjective objective){
        List<Optional<ServerConnection>> connections = players.parallelStream().map(Player::getCurrentServer).distinct().toList();
        connections.forEach(connection ->
                connection.ifPresent(serverConnection ->
                        serverConnection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, objective.toByteArray(name, CUD.CREATE))));
    }

    protected void updateObjective(ScoreboardObjective objective){
        List<Optional<ServerConnection>> connections = players.parallelStream().map(Player::getCurrentServer).distinct().toList();
        connections.forEach(connection ->
                connection.ifPresent(serverConnection ->
                        serverConnection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, objective.toByteArray(name, CUD.UPDATE))));
    }

    protected void createScore(ScoreboardScore score){
        List<Optional<ServerConnection>> connections = players.parallelStream().map(Player::getCurrentServer).distinct().toList();
        connections.forEach(connection ->
                connection.ifPresent(serverConnection ->
                        serverConnection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, score.toByteArray(name, CUD.CREATE))));
    }

    protected void updateScore(ScoreboardScore score){
        List<Optional<ServerConnection>> connections = players.parallelStream().map(Player::getCurrentServer).distinct().toList();
        connections.forEach(connection ->
                connection.ifPresent(serverConnection ->
                        serverConnection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, score.toByteArray(name, CUD.UPDATE))));

    }
}
