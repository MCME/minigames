package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.scoreboard.generics.*;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 *
 * @author Jubo
 */
public class QuizGameScoreboard extends AbstractGameScoreboard {
    protected MiniMessage mm = MiniMessage.miniMessage();
    private final ScoreboardObjective scoresObjective = new ScoreboardObjective(),timerObjective = new ScoreboardObjective();

    private final ScoreboardScore answerTime = new ScoreboardScore(), playersUnfinishedScore = new ScoreboardScore();

    private final  HashMap<Player, ScoreboardScore> scores = new HashMap<>();

    private int questionCount = 0;
    private int currentQuestion = 0;

    private ScheduledTask timerTask;

    private Supplier<Component> questionCounter = () -> Component.text(String.format("Question %d / %d", currentQuestion, questionCount));

    public QuizGameScoreboard(String name, Player creator) {
        super(name, creator);

        timerObjective.setObjectiveName("timer");
        timerObjective.setDisplayName(questionCounter.get());
        timerObjective.setPosition(ScoreboardObjective.Position.DISABLED);

        answerTime.setObjectiveName(timerObjective.getObjectiveName());
        answerTime.setScoreName("answerTime");
        answerTime.setDisplayName(mm.deserialize("<yellow>Time remaining:</yellow>"));
        answerTime.setValue(0);

        playersUnfinishedScore.setObjectiveName(timerObjective.getObjectiveName());
        playersUnfinishedScore.setScoreName("playersUnfinished");
        playersUnfinishedScore.setDisplayName(mm.deserialize("<red>Players thinking:</red>"));
        playersUnfinishedScore.setValue(0);

        scoresObjective.setObjectiveName("scores");
        scoresObjective.setDisplayName(questionCounter.get());
        scoresObjective.setPosition(ScoreboardObjective.Position.SIDE);

        creator.getCurrentServer().ifPresent(connection -> {
            connection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, timerObjective.toByteArray(name, CUD.CREATE));
            connection.sendPluginMessage(ScoreboardScore.IDENTIFIER, playersUnfinishedScore.toByteArray(name, CUD.CREATE));
            connection.sendPluginMessage(ScoreboardScore.IDENTIFIER, answerTime.toByteArray(name, CUD.CREATE));
            connection.sendPluginMessage(ScoreboardObjective.IDENTIFIER, scoresObjective.toByteArray(name, CUD.CREATE));
        });
    }

    public void startQuestion(int time, int players){
        currentQuestion++;
        updateQuestionCounters();

        timerObjective.setPosition(ScoreboardObjective.Position.SIDE);
        updateObjective(timerObjective);

        scoresObjective.setPosition(ScoreboardObjective.Position.DISABLED);
        updateObjective(scoresObjective);

        answerTime.setValue(time);
        updateScore(answerTime);

        playersUnfinishedScore.setValue(players);
        updateScore(playersUnfinishedScore);
        timerTask = MiniGamesPlugin.getInstance().getProxyServer().getScheduler().buildTask(MiniGamesPlugin.getInstance(), () -> {
            answerTime.setValue(answerTime.getValue()-1);
            updateScore(answerTime);
            if(answerTime.getValue()<1){
                cancelTimerTask();
            }
        }).repeat(1,TimeUnit.SECONDS).schedule();
    }

    private void cancelTimerTask(){
        updateQuestionCounters();
        scoresObjective.setPosition(ScoreboardObjective.Position.SIDE);
        updateObjective(scoresObjective);

        timerObjective.setPosition(ScoreboardObjective.Position.DISABLED);
        updateObjective(timerObjective);

        for(ScoreboardScore score: scores.values())
            updateScore(score);

        if(timerTask != null)
            timerTask.cancel();
    }

    @Override
    public void addPlayer(Player player){
        super.addPlayer(player);
        ScoreboardScore score = new ScoreboardScore();
        score.setObjectiveName(scoresObjective.getObjectiveName());
        score.setScoreName(name);
        score.setValue(0);
        score.setDisplayName(Component.text(player.getUsername()));
        scores.put(player,score);

        updateQuestionCounters();
        createScore(score);
    }

    @Override
    public void removePlayer(Player player){
        super.removePlayer(player);
        scores.remove(player);
    }

    public void score(Player player){
        scores.get(player).setValue(scores.get(player).getValue()+1);
        for(ScoreboardScore score:scores.values())
            updateScore(score);
    }

    private void updateQuestionCounters(){
        scoresObjective.setDisplayName(questionCounter.get());
        timerObjective.setDisplayName(questionCounter.get());
        updateObjective(scoresObjective);
        updateObjective(timerObjective);
    }

    public void addQuestion(){
        questionCount++;
        updateQuestionCounters();
    }

    public void removeQuestion(){
        questionCount--;
        updateQuestionCounters();
    }

    public void restart(){
        currentQuestion = 0;
        questionCount = 0;
        updateQuestionCounters();
        for(ScoreboardScore score: scores.values()) {
            score.setValue(0);
            updateScore(score);
        }
    }

    public void clearQuestions(){
        questionCount = 0;
        restart();
    }

    public void updateQuiz(Integer questionCount){
        this.questionCount = questionCount;
        updateQuestionCounters();
        for(ScoreboardScore score: scores.values()) {
            score.setValue(0);
            updateScore(score);
        }
    }

    public void stopQuestion(){
        cancelTimerTask();
    }

    public int getScore(Player player){
        return scores.get(player).getValue();
    }

    public void playerFinished(){
        playersUnfinishedScore.setValue(playersUnfinishedScore.getValue()-1);
        updateScore(playersUnfinishedScore);
    }

    //TODO: add server switching
    public void switchServer(Player player){
//        super.switchServer(player);
//        player.unsafe().sendPacket(quizObjective);
//        player.unsafe().sendPacket(quizDisplay);
//        player.unsafe().sendPacket(scores.get(player));
    }
}
