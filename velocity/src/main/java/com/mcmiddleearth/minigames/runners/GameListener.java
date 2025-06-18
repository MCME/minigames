package com.mcmiddleearth.minigames.runners;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class GameListener {
    final protected @NotNull GameRunner runner;

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
