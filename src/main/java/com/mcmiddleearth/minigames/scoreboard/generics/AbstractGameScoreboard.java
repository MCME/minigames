package com.mcmiddleearth.minigames.scoreboard.generics;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

/**
 *
 * @author Jubo
 */
public abstract class AbstractGameScoreboard {
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("minigames:scoreboard");
    protected enum CAR {CREATE, ADD, REMOVE}

    protected String name;
    protected final List<Player> players = new ArrayList<>();
    protected ScoreboardObjective playerCountObjective;
    protected ScoreboardScore playerCountScore;

    public AbstractGameScoreboard(String name){
        this.name = name;
        playerCountObjective = new ScoreboardObjective();
        playerCountObjective.setDisplayName(Component.text(this.name));
        playerCountObjective.setObjectiveName("PlayerCount");
        playerCountScore = new ScoreboardScore();
        playerCountScore.setObjectiveName("PlayerCount");
        playerCountScore.setScoreName(NamedTextColor.BLUE + "players ");
        playerCountScore.setValue(0);
    }

    public void removePlayer(Player player){
        players.remove(player);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(CAR.REMOVE.ordinal());
        player.sendPluginMessage(IDENTIFIER, out.toByteArray());
        playerCountScore.updateValue(-1);
        updateScore(playerCountScore);
    }

    public void addPlayer(Player player){
        players.add(player);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(CAR.ADD.ordinal());
        out.writeUTF(name);
        player.sendPluginMessage(IDENTIFIER, out.toByteArray());
        playerCountScore.updateValue(+1);
        updateScore(playerCountScore);
    }

    protected void updateObjective(ScoreboardObjective objective){
        List<Optional<ServerConnection>> connections = players.parallelStream().map(Player::getCurrentServer).distinct().toList();
        connections.forEach(connection ->
                connection.ifPresent(serverConnection ->
                        serverConnection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, objective.toByteArray(name, CUD.UPDATE))));
    }

    protected void updateScore(ScoreboardScore score){
        List<Optional<ServerConnection>> connections = players.parallelStream().map(Player::getCurrentServer).distinct().toList();
        connections.forEach(connection ->
                connection.ifPresent(serverConnection ->
                        serverConnection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, score.toByteArray(name, CUD.UPDATE))));

    }
}
