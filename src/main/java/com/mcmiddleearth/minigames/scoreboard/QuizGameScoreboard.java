package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Jubo
 */
public class QuizGameScoreboard extends AbstractGameScoreboard {

    private ScoreboardObjective quizObjective,timerObjective;

    private ScoreboardDisplay quizDisplay,timerDisplay;

    private HashMap<ProxiedPlayer,ScoreboardScore> scores = new HashMap<>();

    private ScoreboardScore answerTime, unfinishedScore;

    private int questionCount = 0;
    private int currentQuestion = 0;

    private final List<String> players = new ArrayList<>();

    private ScheduledTask timerTask;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/ddHH:mm:ss");
    private String currentName;

    private final String name = ComponentSerializer.toString(TextComponent.fromLegacyText("Quiz"));
    private final String name2 = ComponentSerializer.toString(TextComponent.fromLegacyText("Quiz2"));

    public QuizGameScoreboard() {

        quizObjective = new ScoreboardObjective();
        quizObjective.setName(name);
        quizObjective.setAction((byte) 0);
        quizObjective.setType(ScoreboardObjective.HealthDisplay.INTEGER);

        timerObjective = new ScoreboardObjective();
        timerObjective.setName(name2);
        timerObjective.setAction((byte) 0);
        timerObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        timerObjective.setType(ScoreboardObjective.HealthDisplay.INTEGER);

        answerTime = new ScoreboardScore();
        answerTime.setScoreName(name2);
        answerTime.setItemName(Style.HIGHLIGHT+"time remaining: ");
        unfinishedScore = new ScoreboardScore();
        unfinishedScore.setScoreName(name2);
        unfinishedScore.setItemName("players thinking ");

        quizDisplay = new ScoreboardDisplay();
        quizDisplay.setPosition((byte) 1);
        quizDisplay.setName(name);

        timerDisplay = new ScoreboardDisplay();
        timerDisplay.setPosition((byte) 1);
        timerDisplay.setName(name2);
    }

    public void startQuestion(int time, int players){
        currentQuestion++;
        updateTimer();
        timerObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        answerTime.setValue(time);
        unfinishedScore.setValue(players);
        updateObjective(timerObjective);
        updateDisplay(timerDisplay);
        updateScore(answerTime);
        updateScore(unfinishedScore);
        timerTask = ProxyServer.getInstance().getScheduler().schedule(MiniGamesPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                answerTime.setValue(answerTime.getValue()-1);
                updateScore(answerTime);
                if(answerTime.getValue()<1){
                    cancelTimerTask();
                }
            }
        },0,1,TimeUnit.SECONDS);
    }

    private void cancelTimerTask(){
        updateQuiz();
        quizObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        updateObjective(quizObjective);
        for(ScoreboardScore score: scores.values())
            updateScore(score);
        updateDisplay(quizDisplay);
        if(timerTask != null)
            timerTask.cancel();
    }

    @Override
    public void addPlayer(ProxiedPlayer player){
        super.addPlayer(player);
        ScoreboardScore score = new ScoreboardScore();
        score.setScoreName(name);
        score.setValue(0);
        score.setItemName(player.getDisplayName());
        scores.put(player,score);

        updateQuiz();
        quizObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        updateObjective(quizObjective);
        updateDisplay(quizDisplay);
        for(ScoreboardScore scoreUpdate: scores.values()){
            updateScore(scoreUpdate);
        }
    }

    @Override
    public void removePlayer(ProxiedPlayer player){
        super.removePlayer(player);
        scores.remove(player);
    }

    public void score(ProxiedPlayer player){
        scores.get(player).setValue(scores.get(player).getValue()+1);
        for(ScoreboardScore score:scores.values())
            updateScore(score);
    }

    private void updateTimer(){
        setCurrentName();
        timerDisplay.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(currentName)));
        timerObjective.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(currentName)));
        answerTime.setScoreName(ComponentSerializer.toString(TextComponent.fromLegacyText(currentName)));
        unfinishedScore.setScoreName(ComponentSerializer.toString(TextComponent.fromLegacyText(currentName)));
    }

    private void updateQuiz(){
        setCurrentName();
        quizObjective.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(currentName)));
        quizDisplay.setName(ComponentSerializer.toString(TextComponent.fromLegacyText(currentName)));
        for(ScoreboardScore score: scores.values())
            score.setScoreName(ComponentSerializer.toString(TextComponent.fromLegacyText(currentName)));
    }

    public void addQuestion(){
        questionCount++;
        updateQuiz();
        quizObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        //timerObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        updateObjective(quizObjective);
        for(ScoreboardScore score: scores.values())
            updateScore(score);
        updateDisplay(quizDisplay);
    }

    public void removeQuestion(){
        questionCount--;
        updateQuiz();
        quizObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        updateObjective(quizObjective);
        for(ScoreboardScore score: scores.values())
            updateScore(score);
        updateDisplay(quizDisplay);
    }

    public void restart(){
        currentQuestion = 0;
        questionCount = 0;
        updateQuiz();
        quizObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        updateObjective(quizObjective);
        for(ScoreboardScore score: scores.values()) {
            score.setValue(0);
            updateScore(score);
        }
        updateDisplay(quizDisplay);
    }

    public void clearQuestions(){
        questionCount = 0;
        restart();
    }

    public void updateQuiz(Integer questionCount){
        this.questionCount = questionCount;
        updateQuiz();
        quizObjective.setValue(ComponentSerializer.toString(TextComponent.fromLegacyText("Question "+currentQuestion+" / " + questionCount)));
        updateObjective(quizObjective);
        for(ScoreboardScore score: scores.values()) {
            score.setValue(0);
            updateScore(score);
        }
        updateDisplay(quizDisplay);
    }

    private void setCurrentName(){
        currentName = ComponentSerializer.toString(TextComponent.fromLegacyText(dtf.format(LocalDateTime.now())));
    }

    public void stopQuestion(){
        cancelTimerTask();
    }

    public int getScore(ProxiedPlayer player){
        return scores.get(player).getValue();
    }

    public void playerFinished(){
        unfinishedScore.setValue(unfinishedScore.getValue()-1);
        updateScore(unfinishedScore);
    }

    @Override
    public void switchServer(ProxiedPlayer player){
        super.switchServer(player);
        player.unsafe().sendPacket(quizObjective);
        player.unsafe().sendPacket(quizDisplay);
        player.unsafe().sendPacket(scores.get(player));
    }
}
