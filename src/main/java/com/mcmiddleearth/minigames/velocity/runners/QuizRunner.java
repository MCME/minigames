package com.mcmiddleearth.minigames.velocity.runners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.common.Channels;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.question.AbstractQuestion;
import com.mcmiddleearth.minigames.velocity.question.NumberQuestion;
import com.mcmiddleearth.minigames.velocity.runners.listeners.GameListener;
import com.mcmiddleearth.minigames.velocity.runners.listeners.PlayerJoinListener;
import com.mcmiddleearth.minigames.velocity.runners.listeners.PlayerLeaveListener;
import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.mcmiddleearth.minigames.velocity.util.MessageUtil;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.audience.Audience;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//TODO: Add plugin listener so the quiz conversations can be checked
public class QuizRunner extends GameRunner{
    private final List<AbstractQuestion> allQuestions = new ArrayList<>();
    private final Queue<AbstractQuestion> questionQueue = new LinkedList<>();
    private AbstractQuestion currentQuestion;

    private int nextQuestion = 0;
    private int ANSWER_TIME_SEC = 30;
    private final List<Player> inQuizConversation = new ArrayList<>();
    private final HashMap<Player, Integer> scores = new HashMap<>();
    private ScheduledTask questionCountDown;

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
        questionQueue.addAll(allQuestions);
        Collections.shuffle((LinkedList<AbstractQuestion>)questionQueue);
        sendQuestion();
    }

    public void sendQuestion(){
        currentQuestion = questionQueue.poll();
        if(currentQuestion == null)
            return;
        inQuizConversation.addAll(players);
        sendConversationStarter();
        sendQuestionToPlayers();
        AtomicInteger countdown = new AtomicInteger(ANSWER_TIME_SEC);
        questionCountDown = MiniGamesPlugin.createTask(() -> {
            countdown.getAndDecrement();
            if(countdown.get() < 1) {
                if (currentQuestion instanceof NumberQuestion)
                    MessageUtil.sendInfoMessage(Audience.audience(inQuizConversation), "Time to answer expired. Correct answer: "
                            + currentQuestion.getCorrectAnswer() +
                            ". Allowed deviation from correct answer was "
                            + ((NumberQuestion) currentQuestion).getPrecision() + ".");
                else
                    MessageUtil.sendInfoMessage(Audience.audience(inQuizConversation), "Time to answer expired. Correct answer: "
                            + currentQuestion.getCorrectAnswer());
                questionCountDown.cancel();
            }
        }).delay(0, TimeUnit.SECONDS).repeat(1,TimeUnit.SECONDS).schedule();
    }

    private void sendConversationStarter(){
        players.forEach(player ->
                player.getCurrentServer().ifPresent(connection ->{
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("start_conversation");
                            out.writeUTF(player.getUsername());
                        connection.sendPluginMessage(Channels.QUIZ, out.toByteArray());
                }));
    }

    private void sendQuestionToPlayers(){
        String question = currentQuestion.getQuestion();
        //TODO: Finish question sending to players
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
