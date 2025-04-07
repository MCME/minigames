package com.mcmiddleearth.minigames.velocity.runners.listeners;

import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.runners.GameRunner;
import org.jetbrains.annotations.NotNull;

public abstract class GameListener {
    final @NotNull GameRunner runner;

    private GameListener(){
        runner = null;
    }

    public GameListener(@NotNull GameRunner runner){
        this.runner = runner;
        MiniGamesPlugin.registerEvent(this);
    }

    public void unregister(){
        MiniGamesPlugin.unregisterEvent(this);
    }

}
