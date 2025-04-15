package com.mcmiddleearth.minigames.velocity.runners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.common.Channels;
import com.mcmiddleearth.minigames.spigot.util.Style;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.velocity.question.*;
import com.mcmiddleearth.minigames.velocity.runners.listeners.AnswerListener;
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
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

//TODO: add scoreboard stuff
public class QuizRunner extends GameRunner{
    private final int ANSWER_TIME_SEC = 30;
    private final List<AbstractQuestion> allQuestions = new ArrayList<>();
    private final Queue<AbstractQuestion> questionQueue = new LinkedList<>();
    private AbstractQuestion currentQuestion;

    private final List<Player> inQuizConversation = new ArrayList<>();
    private final HashMap<Player, Integer> scores = new HashMap<>();
    private ScheduledTask questionCountDown;

    private boolean randomChoices = false;
    private boolean canMultipleWin = false;

    @Override
    public void initialise(String name, Player manager) {
        scoreboard = new QuizGameScoreboard(name, manager);
        this.manager = manager;
        listeners.add(new PlayerLeaveListener(this));
        listeners.add(new PlayerJoinListener(this));
        listeners.add(new AnswerListener(this));
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
                inQuizConversation.clear();
                questionCountDown.cancel();
                currentQuestion = questionQueue.poll();
                if(currentQuestion == null)
                    finishQuiz();
            }
        }).delay(0, TimeUnit.SECONDS).repeat(1,TimeUnit.SECONDS).schedule();
    }

    private void sendConversationStarter(){
        players.forEach(player ->
                player.getCurrentServer().ifPresent(connection ->{
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("start_conversation");
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
    public void Answer(Player player, String answer){
        switch(currentQuestion){
            case SingleChoiceQuestion _ : if(answer.length() != 1) {
                MessageUtil.sendErrorMessage(player, "Invalid answer. You did not type in a single answer letter.");
                player.getCurrentServer().ifPresent(connection ->{
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("start_conversation");
                    connection.sendPluginMessage(Channels.QUIZ, out.toByteArray());
                });
                return;
            }
            break;
            case NumberQuestion _ : if(!StringUtils.isNumeric(answer)){
                MessageUtil.sendErrorMessage(player, "Invalid answer. You did not type in a whole number.");
                player.getCurrentServer().ifPresent(connection ->{
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("start_conversation");
                    connection.sendPluginMessage(Channels.QUIZ, out.toByteArray());
                });
                return;
            }
            default: break;
        }
        inQuizConversation.remove(player);
        player.sendMessage(Component.text("[Your answer] "+answer).color(NamedTextColor.AQUA));
        if (currentQuestion.isCorrectAnswer(answer)) {
            scores.put(player, scores.getOrDefault(player, 0) + 1);
            if (currentQuestion instanceof NumberQuestion && !answer.equals(currentQuestion.getCorrectAnswer()))
                MessageUtil.sendInfoMessage(player, "Almost! The right answer was " + currentQuestion.getCorrectAnswer() + " but you were close enough.");
            else
                MessageUtil.sendInfoMessage(player, "You answered this Question correctly.");
        } else {
            if (currentQuestion instanceof NumberQuestion)
                MessageUtil.sendInfoMessage(player, "You failed to answer this Question correctly. Correct answer was "
                        + currentQuestion.getCorrectAnswer() + ". Allowed deviation from correct answer was " + ((NumberQuestion) currentQuestion).getPrecision() + ".");
            else
                MessageUtil.sendInfoMessage(player, "You failed to answer this Question correctly. Correct answer: " + currentQuestion.getCorrectAnswer());
        }
        if(inQuizConversation.isEmpty())
            allAnswered();
    }

    private void allAnswered(){
        questionCountDown.cancel();
        currentQuestion = questionQueue.poll();
        if(currentQuestion == null)
            finishQuiz();
    }

    private void finishQuiz(){
        Integer maxScore = scores.values().stream().max(Integer::compare).orElseThrow();
        Set<Player> winners = players.stream().filter(player ->
                Objects.equals(scores.getOrDefault(player, -1), maxScore)).collect(Collectors.toSet());
        if(winners.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "There is no winner, something went wrong, please report this in dev-public.");
        }
        if(winners.size() > 1 && !canMultipleWin){
            MessageUtil.sendInfoMessage(manager, "There's multiple winners, add another question or use /quiz winner.");
        }
        Audience.audience(winners).sendMessage(Component.text("Congrats, You won the quiz.").color(NamedTextColor.GOLD));
        MessageUtil.sendInfoMessage(Audience.audience(players),
                "Game Over, "+String.join(", ", players.stream().map(Player::getUsername).collect(Collectors.toSet()))+" won the quiz.");
    }

    @Override
    public void restart() {
        questionCountDown.cancel();
        scores.clear();
        inQuizConversation.clear();

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
        if(inQuizConversation.remove(player) && inQuizConversation.isEmpty())
            allAnswered();
    }

    @Override
    public boolean canJoin(Player player) {
        return inQuizConversation.isEmpty();
    }

    public void toggleMultipleWinners(){
        canMultipleWin = !canMultipleWin;
    }
    public void toggleRandomChoices(){
        randomChoices = !randomChoices;
    }
}
