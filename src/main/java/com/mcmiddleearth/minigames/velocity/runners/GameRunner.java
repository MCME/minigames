package com.mcmiddleearth.minigames.velocity.runners;

import com.velocitypowered.api.proxy.Player;

import java.util.HashSet;
import java.util.Set;

public abstract class GameRunner {
    protected Player manager;
    protected Set<Player> players = new HashSet<>();
    protected Set<Player> spectators = new HashSet<>();

    abstract public void initialise(String name);
    abstract public boolean canStart();
    abstract public void start();
    abstract public void restart();
    abstract public void end();

    abstract public void join(Player player);
    abstract public boolean canJoin(Player player);
}
