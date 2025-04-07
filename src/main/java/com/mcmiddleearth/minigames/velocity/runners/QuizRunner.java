package com.mcmiddleearth.minigames.velocity.runners;

import com.mcmiddleearth.minigames.velocity.question.AbstractQuestion;
import com.mcmiddleearth.minigames.velocity.runners.listeners.GameListener;
import com.mcmiddleearth.minigames.velocity.runners.listeners.PlayerJoinListener;
import com.mcmiddleearth.minigames.velocity.runners.listeners.PlayerLeaveListener;
import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.velocitypowered.api.proxy.Player;

import java.util.*;

public class QuizRunner extends GameRunner{
    private final List<AbstractQuestion> allQuestions = new ArrayList<>();
    private final Queue<AbstractQuestion> questionQueue = new LinkedList<>();
    private AbstractQuestion currentQuestion;

    private int nextQuestion = 0;
    private int ANSWER_TIME_SEC = 30;
    private final List<Player> inQuizConversation = new ArrayList<>();
    private final HashMap<Player, Integer> scores = new HashMap<>();

    @Override
    public void initialise(String name, Player manager) {
        scoreboard = new QuizGameScoreboard(name, manager);
        this.manager = manager;
        listeners.add(new PlayerLeaveListener(this));
        listeners.add(new PlayerJoinListener(this));
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
        listeners.forEach(GameListener::unregister);
    }

    @Override
    public void join(Player player) {
        players.add(player);
        scoreboard.addPlayer(player);
        sendJoinMessage(player);
    }

    @Override
    public void leave(Player player){
        players.remove(player);
        scoreboard.removePlayer(player);
        if(player == manager) {
            initSelfDestruct();
            return;
        }
        sendLeaveMessage(player);
    }

    @Override
    public boolean canJoin(Player player) {
        return false;
    }
}
