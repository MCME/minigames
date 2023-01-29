package com.mcmiddleearth.minigames.scoreboard;


import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jubo
 */
public abstract class AbstractGameScoreboard {

    private final List<ProxiedPlayer> players = new ArrayList<>();

    public AbstractGameScoreboard(){}

    protected void updateObjective(ScoreboardObjective objective){
        for(ProxiedPlayer player :players)
            player.unsafe().sendPacket(objective);
    }

    protected void updateScore(ScoreboardScore score){
        for(ProxiedPlayer player: players)
            player.unsafe().sendPacket(score);
    }

    protected void updateDisplay(ScoreboardDisplay display){
        for(ProxiedPlayer player: players){
            player.unsafe().sendPacket(display);
        }
    }

    public void addPlayer(ProxiedPlayer player){
        players.add(player);
    }

    public void removePlayer(ProxiedPlayer player){
        players.remove(player);

        ScoreboardObjective objective = new ScoreboardObjective();
        objective.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(player.getName())));
        objective.setAction((byte) 0);
        objective.setType(ScoreboardObjective.HealthDisplay.INTEGER);
        objective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("")));

        ScoreboardDisplay display = new ScoreboardDisplay();
        display.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(player.getName())));
        display.setPosition((byte) 1);

        player.unsafe().sendPacket(objective);
        player.unsafe().sendPacket(display);
    }

    public void switchServer(ProxiedPlayer player){}
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
