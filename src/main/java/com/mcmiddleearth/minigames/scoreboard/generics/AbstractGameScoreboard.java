package com.mcmiddleearth.minigames.scoreboard.generics;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Jubo
 */
public abstract class AbstractGameScoreboard {
    private final List<Player> players = new ArrayList<>();

    public AbstractGameScoreboard(){}

    protected void updateObjective(ScoreboardObjective objective){
        Optional<ServerConnection> connection = players.get(0).getCurrentServer();
        objective.setAction(CRUD.UPDATE);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(objective.toString());
        connection.ifPresent(serverConnection -> serverConnection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, out.toByteArray()));
    }

//    protected void updateScore(ScoreboardScore score){
//        for(Player player: players)
//            player.unsafe().sendPacket(score);
//    }
//
//    protected void updateDisplay(ScoreboardDisplay display){
//        for(Player player: players){
//            player.unsafe().sendPacket(display);
//        }
//    }
//
//    public void addPlayer(Player player){
//        players.add(player);
//    }
//
//    public void removePlayer(Player player){
//        players.remove(player);
//
//        ScoreboardObjective objective = new ScoreboardObjective();
//        objective.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(player.getName())));
//        objective.setAction((byte) 0);
//        objective.setType(ScoreboardObjective.HealthDisplay.INTEGER);
//        objective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("")));
//
//        ScoreboardDisplay display = new ScoreboardDisplay();
//        display.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(player.getName())));
//        display.setPosition((byte) 1);
//
//        player.unsafe().sendPacket(objective);
//        player.unsafe().sendPacket(display);
//    }
//
//    public void switchServer(Player player){}
    /*
    public void incrementPlayer(){
        playerCountScore.setValue(playerCountScore.getValue()+1);
    }

    public void decrementPlayer(){
        playerCountScore.setValue(playerCountScore.getValue()-1);
    }

    protected int getPlayerCount(){
        return playerCountScore.getValue();
    }



    protected  ScoreboardScore getPlayerCountScore(){
        return playerCountScore;
    }

    protected ScoreboardDisplay getPlayerCountDisplay(){
        return playerCountDisplay;
    }

     */






        /*

    public void create(){
        ScoreboardObjective objective = new ScoreboardObjective();
        objective.setName(ComponentSerializer.toString(TextComponent.fromLegacyText("test")));
        objective.setAction((byte) 0);
        objective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText(name)));
        objective.setType(ScoreboardObjective.HealthDisplay.INTEGER);
        player.unsafe().sendPacket(objective);

        playerCountScore = new ScoreboardScore();;
        playerCountScore.setItemName(player.getDisplayName());
        playerCountScore.setValue(this.score);
        playerCountScore.setScoreName(ComponentSerializer.toString(TextComponent.fromLegacyText("test")));
        playerCountScore.setAction((byte) 0);
        player.unsafe().sendPacket(score);

        ScoreboardDisplay display = new ScoreboardDisplay();
        display.setName(ComponentSerializer.toString(TextComponent.fromLegacyText("test")));
        display.setPosition((byte) 1);
        player.unsafe().sendPacket(display);
    }

     */
}
