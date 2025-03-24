package com.mcmiddleearth.minigames.velocity.runners;

import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.velocitypowered.api.proxy.Player;

public class QuizRunner extends GameRunner{
    @Override
    public void initialise(String name, Player manager) {
        scoreboard = new QuizGameScoreboard(name, manager);
        this.manager = manager;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void end() {

    }

    @Override
    public void join(Player player) {

    }

    @Override
    public boolean canJoin(Player player) {
        return false;
    }
}
