package com.mcmiddleearth.minigames.runners.quiz;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.Style;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.VelocityChannels;
import com.mcmiddleearth.minigames.question.*;
import com.mcmiddleearth.minigames.runners.GameRunner;
import com.mcmiddleearth.minigames.runners.quiz.listeners.AnswerListener;
import com.mcmiddleearth.minigames.runners.GameListener;
import com.mcmiddleearth.minigames.runners.quiz.listeners.PlayerJoinListener;
import com.mcmiddleearth.minigames.runners.quiz.listeners.PlayerLeaveListener;
import com.mcmiddleearth.minigames.util.MessageUtil;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QuizRunner extends GameRunner {
    public int ANSWER_TIME_SEC = 30;
    public final List<AbstractQuestion> allQuestions = new ArrayList<>();
    public final Queue<AbstractQuestion> questionQueue = new LinkedList<>();
    private AbstractQuestion currentQuestion;

    public final List<Player> inQuizConversation = new ArrayList<>();
    public final HashMap<Player, Integer> scores = new HashMap<>();
    private ScheduledTask questionCountDown;

    private final QuizScoreboardEditor scoreboardEditor;

    public QuizRandomness randomness = QuizRandomness.OFF;
    //TODO: add a command to toggle multiple winners
    private boolean canMultipleWin = false;

    public QuizRunner(String name, Player manager) {
        super(name, manager);
        scoreboardEditor = new QuizScoreboardEditor(this);
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
        if(randomness == QuizRandomness.ALL || randomness == QuizRandomness.QUESTION_ORDER)
            Collections.shuffle((LinkedList<AbstractQuestion>)questionQueue);

        players.forEach(scoreboardEditor::initScoreboard);
    }

    public void sendQuestion(){
        sendQuestion(ANSWER_TIME_SEC);
    }

    public void sendQuestion(int answer_time_sec){
        currentQuestion = questionQueue.poll();
        if(currentQuestion == null) {
            MessageUtil.sendErrorMessage(manager, "There are no more questions, add more if you want to continue.");
            return;
        }
        inQuizConversation.addAll(players);
        sendConversationStarter();
        sendQuestionToPlayers();
        AtomicInteger countdown = new AtomicInteger(answer_time_sec);
        scoreboardEditor.updateTimer(countdown.get());
        scoreboardEditor.switchMode();
        scoreboardEditor.updateTitle();
        questionCountDown = MiniGamesPlugin.createTask(() -> {
            scoreboardEditor.updateTimer(countdown.get());
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
                if(questionQueue.peek() == null)
                    finishQuiz();
                scoreboardEditor.updateScores();
                scoreboardEditor.switchMode();
            }
        }).delay(0, TimeUnit.SECONDS).repeat(1,TimeUnit.SECONDS).schedule();

    }

    private void sendConversationStarter(){
        players.forEach(player ->
                player.getCurrentServer().ifPresent(connection ->{
                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("start_conversation");
                        connection.sendPluginMessage(VelocityChannels.QUIZ, out.toByteArray());
                }));
    }

    private void sendQuestionToPlayers(){
        String question = currentQuestion.getQuestion();
        String[] questionAnswer = null;
        if(currentQuestion instanceof ChoiceQuestion){
            if(randomness == QuizRandomness.ALL || randomness == QuizRandomness.ANSWER_ORDER)
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
        Component hint = getHint();
        if(hint == null)
                throw new IllegalStateException("Unexpected value: " + currentQuestion);
        Audience.audience(players).sendMessage(Component.text("[Hint] ").color(NamedTextColor.DARK_GREEN).append(hint));
        }

    private @Nullable Component getHint() {
        Component hint = null;
        if(currentQuestion instanceof SingleChoiceQuestion)
            hint = Component.text("Type in chat the letter of the correct answer. \n").color(NamedTextColor.DARK_GREEN).append(
                    Component.text("Only one").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD).append(
                            Component.text(" answer is correct.").color(NamedTextColor.DARK_GREEN)));
        if(currentQuestion instanceof NumberQuestion)
            hint =  Component.text("Type in chat a whole number.").color(NamedTextColor.DARK_GREEN);
        if(currentQuestion instanceof FreeQuestion)
            hint =  Component.text("Type your answer in chat.").color(NamedTextColor.DARK_GREEN);
        if(currentQuestion instanceof ChoiceQuestion)
            hint = Component.text("Type in the letters of the correct answers. \n").color(NamedTextColor.DARK_GREEN).append(
                        Component.text("More than one").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).append(
                                Component.text(" answer may be correct.").color(NamedTextColor.DARK_GREEN)));
        return hint;
    }

    public void Answer(Player player, String answer){
        if(currentQuestion instanceof SingleChoiceQuestion && answer.length() != 1){
            MessageUtil.sendErrorMessage(player, "Invalid answer. You did not type in a single answer letter.");
            player.getCurrentServer().ifPresent(connection ->{
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("start_conversation");
                connection.sendPluginMessage(VelocityChannels.QUIZ, out.toByteArray());
            });
            return;
        }
        if(currentQuestion instanceof NumberQuestion && !StringUtils.isNumeric(answer)){
                MessageUtil.sendErrorMessage(player, "Invalid answer. You did not type in a whole number.");
                player.getCurrentServer().ifPresent(connection ->{
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("start_conversation");
                    connection.sendPluginMessage(VelocityChannels.QUIZ, out.toByteArray());
                });
                return;
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
        scoreboardEditor.updateThinking();
    }

    private void allAnswered(){
        questionCountDown.cancel();
        if(questionQueue.peek() == null)
            finishQuiz();
        scoreboardEditor.updateScores();
        scoreboardEditor.switchMode();
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

    public void announceWinners(){
        Integer maxScore = scores.values().stream().max(Integer::compare).orElseThrow();
        Set<Player> winners = players.stream().filter(player ->
                Objects.equals(scores.getOrDefault(player, -1), maxScore)).collect(Collectors.toSet());
        Audience.audience(winners).sendMessage(Component.text("Congrats, You won the quiz.").color(NamedTextColor.GOLD));
        if(winners.isEmpty()){
            MessageUtil.sendErrorMessage(manager, "There is no winner, something went wrong, please report this in dev-public.");
        }
        MessageUtil.sendInfoMessage(Audience.audience(players),
                "Game Over, "+String.join(", ", players.stream().map(Player::getUsername).collect(Collectors.toSet()))+" won the quiz.");
    }

    @Override
    public void restart() {
        if(questionCountDown != null)
            questionCountDown.cancel();
        scores.clear();
        inQuizConversation.clear();
        start();
    }

    public void clearQuestions(){
        questionQueue.clear();
        allQuestions.clear();
    }

    @Override
    public void end() {
        listeners.forEach(GameListener::unregister);
        players.forEach(scoreboardEditor :: removePlayer);
    }

    @Override
    public void join(Player player) {
        players.add(player);
        scoreboardEditor.addPlayer(player);
        sendJoinMessage(player);
    }

    @Override
    public void leave(Player player){
        players.remove(player);
        scoreboardEditor.removePlayer(player);
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
}
