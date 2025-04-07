package com.mcmiddleearth.minigames.velocity.runners.listeners;

import com.mcmiddleearth.minigames.velocity.runners.GameRunner;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener extends GameListener{
    public PlayerJoinListener(@NotNull GameRunner runner) {
        super(runner);
    }

    @Subscribe
    public void onJoin(PostLoginEvent e){
        Player player = e.getPlayer();
        if(runner.getManager() == player) {
            runner.cancelSelfDestruct();
        }
    }
}
