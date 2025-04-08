package com.mcmiddleearth.minigames.velocity.runners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.common.Channels;
import com.mcmiddleearth.minigames.spigot.util.Style;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.question.*;
import com.mcmiddleearth.minigames.velocity.runners.listeners.GameListener;
import com.mcmiddleearth.minigames.velocity.runners.listeners.PlayerJoinListener;
import com.mcmiddleearth.minigames.velocity.runners.listeners.PlayerLeaveListener;
import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.mcmiddleearth.minigames.velocity.util.MessageUtil;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//TODO: Add plugin listener so the quiz conversations can be checked
public class QuizRunner extends GameRunner{
    private final int ANSWER_TIME_SEC = 30;
    private final List<AbstractQuestion> allQuestions = new ArrayList<>();
    private final Queue<AbstractQuestion> questionQueue = new LinkedList<>();
    private AbstractQuestion currentQuestion;

    private int nextQuestion = 0;
    private boolean randomChoices = false;
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
        String[] questionAnswer = null;
        if(currentQuestion instanceof ChoiceQuestion){
            if(randomChoices)
                questionAnswer = ((ChoiceQuestion) currentQuestion).getInRandomOrder();
            else
                questionAnswer = ((ChoiceQuestion) currentQuestion).getInProperOrder();
        }
        if(questionAnswer != null) {
            for(String answer: questionAnswer){
                Audience.audience(players).sendMessage(Component.text(question));
                char answerIndex = answer.charAt(0);
                String answerTemp = answer.substring(1);
                Audience.audience(players).sendMessage(
                        Component.text("["+answerIndex+"] ").color(Style.HIGHLIGHT).append(
                                Component.text(answerTemp).color(Style.INFO)));
            }
        }
        Component hint = switch(currentQuestion){
            case SingleChoiceQuestion _ :
                yield Component.text("Type in chat the letter of the correct answer. \n").color(NamedTextColor.DARK_GREEN).append(
                        Component.text("Only one").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).append(
                                Component.text(" answer is correct.").color(NamedTextColor.DARK_GREEN)));
            case NumberQuestion _ :
                yield Component.text("Type in chat a whole number.").color(NamedTextColor.DARK_GREEN);
            case FreeQuestion _ :
                yield Component.text("Type your answer in chat.").color(NamedTextColor.DARK_GREEN);
            case ChoiceQuestion _ :
                yield Component.text("Type in the letters of the correct answers. \n").color(NamedTextColor.DARK_GREEN).append(
                        Component.text("More than one").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).append(
                                Component.text(" answer may be correct.").color(NamedTextColor.DARK_GREEN)));
            default:
                throw new IllegalStateException("Unexpected value: " + currentQuestion);
        };
        Audience.audience(players).sendMessage(Component.text("[Hint] ").color(NamedTextColor.DARK_GREEN).append(hint));
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
