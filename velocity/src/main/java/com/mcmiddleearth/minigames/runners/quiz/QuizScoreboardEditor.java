package com.mcmiddleearth.minigames.runners.quiz;

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.*;
import net.kyori.adventure.text.Component;

import java.util.function.Consumer;

public class QuizScoreboardEditor {

    private final QuizRunner runner;

    private final String timer = "timer";
    private final String remaining = "time remaining:";
    private final String thinking = "players thinking:";
    private final String scores = "scores";

    private boolean inQuestion = false;

    public QuizScoreboardEditor(QuizRunner runner){
        this.runner = runner;
    }

    public void initScoreboard(Player player){
        applyFunction(player, scoreboard -> {
            if(inQuestion) {
                scoreboard.registerObjective(scoreboard.objectiveBuilder(scores));
                scoreboard.registerObjective(scoreboard.objectiveBuilder(timer).displaySlot(DisplaySlot.SIDEBAR));
            } else {
                scoreboard.registerObjective(scoreboard.objectiveBuilder(scores).displaySlot(DisplaySlot.SIDEBAR));
                scoreboard.registerObjective(scoreboard.objectiveBuilder(timer));
            }
            updateTitle();
        });
    }

    public void updateTitle(){
        applyFunction(scoreboard ->{
            TextHolder holder = TextHolder.of(Component.text(
                    String.format("Question %d / %d", runner.allQuestions.size()-runner.questionQueue.size(), runner.allQuestions.size())));
            scoreboard.getObjective(scores).setTitle(holder);
            scoreboard.getObjective(timer).setTitle(holder);

        });
    }

    public void updateScores(){
        applyFunction(scoreboard -> {
            ProxyObjective scoreObjective = scoreboard.getObjective(scores);
            runner.scores.forEach((player, integer) ->
                    scoreObjective.setScore(player.getUsername(), builder -> builder.score(integer)));
        });
    }

    public void updateTimer(int timeLeft){
        applyFunction(scoreboard ->
            scoreboard.getObjective(timer).setScore(remaining, builder -> builder.score(timeLeft))
        );
    }

    public void updateThinking(){
        applyFunction(scoreboard ->
            scoreboard.getObjective(timer).setScore(thinking, builder -> builder.score(runner.inQuizConversation.size()))
        );
    }

    public void switchMode(){
        applyFunction(scoreboard -> {
            if(inQuestion) {
                scoreboard.registerObjective(scoreboard.objectiveBuilder(scores));
                scoreboard.registerObjective(scoreboard.objectiveBuilder(timer).displaySlot(DisplaySlot.SIDEBAR));
            } else {
                scoreboard.registerObjective(scoreboard.objectiveBuilder(scores).displaySlot(DisplaySlot.SIDEBAR));
                scoreboard.registerObjective(scoreboard.objectiveBuilder(timer));
            }
        });
        inQuestion = !inQuestion;
    }

    public void addPlayer(Player player){
        initScoreboard(player);
        applyFunction(scoreboard ->
            scoreboard.getObjective(scores).setScore(player.getUsername(), (builder -> builder.score(0)))
        );
    }

    public void removePlayer(Player player){
        applyFunction(scoreboard ->
                scoreboard.getObjective(scores).removeScore(player.getUsername())
        );
        ScoreboardManager.getInstance().getProxyScoreboard(player).unregisterObjective(scores);
        ScoreboardManager.getInstance().getProxyScoreboard(player).unregisterObjective(timer);
    }

    private void applyFunction(Consumer<ProxyScoreboard> function){
        runner.getPlayers().forEach(player ->
                applyFunction(player, function));
    }

    private void applyFunction(Player player, Consumer<ProxyScoreboard> function){
        function.accept(ScoreboardManager.getInstance().getProxyScoreboard(player));
    }

}
