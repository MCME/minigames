package com.mcmiddleearth.minigames.velocity.runners;

import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.runners.listeners.GameListener;
import com.mcmiddleearth.minigames.velocity.scoreboard.generics.AbstractGameScoreboard;
import com.mcmiddleearth.minigames.velocity.util.MessageUtil;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class GameRunner {
    protected Player manager;
    protected Set<Player> players = new HashSet<>();
    protected AbstractGameScoreboard scoreboard;
    protected Set<GameListener> listeners = new HashSet<>();
    protected ScheduledTask selfDestruct;

    abstract public void initialise(String name, Player manager);
    abstract public boolean canStart();
    abstract public void start();
    abstract public void restart();
    abstract public void end();

    abstract public void join(Player player);
    abstract public void leave(Player player);
    abstract public boolean canJoin(Player player);

    protected void initSelfDestruct() {
        MessageUtil.sendMessage(players, "The host was disconnected from the server. This quiz will end in 60 seconds.");
        selfDestruct = MiniGamesPlugin.schedule(() -> {
            if (!manager.isActive())
                end();
        }, 60, TimeUnit.SECONDS);
    }

    public void cancelSelfDestruct(){
        selfDestruct.cancel();
    }

    protected void sendJoinMessage(@NotNull Player player){
        MessageUtil.sendMessage(players, "Everybody welcome " + player.getUsername() + " to the game!");
        MessageUtil.sendMessage(player, "Welcome to the game!");
    }

    protected void sendLeaveMessage(@NotNull Player player){
        MessageUtil.sendMessage(players, player.getUsername() + " has left the game.");
    }

    public Player getManager(){return manager;}
    public Set<Player> getPlayers(){return players;}
}
