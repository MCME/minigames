package com.mcmiddleearth.minigames.runners.quiz.listeners;

import com.mcmiddleearth.minigames.runners.GameRunner;
import com.mcmiddleearth.minigames.runners.GameListener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveListener extends GameListener {

    public PlayerLeaveListener(@NotNull GameRunner runner) {
        super(runner);
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event){
        Player player = event.getPlayer();
        if(runner.getPlayers().contains(player))
            runner.leave(player);
    }
}
