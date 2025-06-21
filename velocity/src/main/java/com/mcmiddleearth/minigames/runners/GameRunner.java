package com.mcmiddleearth.minigames.runners;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.util.MessageUtil;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class GameRunner {
    protected Player manager;
    protected Set<Player> players = new HashSet<>();
    protected Set<GameListener> listeners = new HashSet<>();
    protected ScheduledTask selfDestruct;

    public GameRunner(String name, Player manager){
        this.manager = manager;
    }

    abstract public boolean canStart();
    abstract public void start();
    abstract public void restart();
    abstract public void end();

    abstract public void join(Player player);
    abstract public void leave(Player player, boolean leftServer);
    abstract public boolean canJoin(Player player);

    protected void initSelfDestruct() {
        MessageUtil.sendErrorMessage(Audience.audience(players), "The host was disconnected from the server. This game will end in 60 seconds.");
        selfDestruct = MiniGamesPlugin.createTask(() -> {
            if (!manager.isActive())
                end();
        }).delay(60, TimeUnit.SECONDS).schedule();
    }

    public void cancelSelfDestruct(){
        selfDestruct.cancel();
    }

    protected void sendJoinMessage(@NotNull Player player){
        MessageUtil.sendInfoMessage(Audience.audience(players), "Everybody welcome " + player.getUsername() + " to the game!");
        MessageUtil.sendInfoMessage(player, "Welcome to the game!");
    }

    protected void sendLeaveMessage(@NotNull Player player){
        MessageUtil.sendInfoMessage(Audience.audience(players), player.getUsername() + " has left the game.");
    }

    public Player getManager(){return manager;}
    public Set<Player> getPlayers(){return players;}
}
