package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.quiz.question.*;
import com.mcmiddleearth.minigames.velocity.scoreboard.QuizGameScoreboard;
import com.mcmiddleearth.minigames.util.NumericUtil;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.StringUtil;
import com.mcmiddleearth.minigames.util.Style;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jubo, Eriol_Eandur
 */
public class QuizGame extends AbstractGame{

    private AbstractQuestion currentQuestion;
    private final List<AbstractQuestion> questions = new ArrayList<>();

    private boolean randomQuestions = true;
    private boolean randomChoices = false;

    private int nextQuestion = 0;

    private int answerTime = 30;

    private final List<Player> inQuizConversation = new ArrayList<>();

    private final HashMap<Player,Integer> scores = new HashMap<>();

    private boolean acceptConversation = false;

    private ScheduledTask timerTask;

    private boolean allAnswered = false;

    public QuizGame(Player manager, String name) {
        super(manager, name,GameType.LORE_QUIZ,new QuizGameScoreboard(name, manager));
    }

    public List<AbstractQuestion> getQuestions() {
        return questions;
    }

    public void setAcceptConversation(boolean acceptConversation){this.acceptConversation = acceptConversation;}

    public boolean acceptConversation(){
        return acceptConversation;
    }

    @Override
    public String getGameChatTag(Player player){
        if(getManager() == player)
            return ChatColor.DARK_AQUA + "<Host ";
        else
            return super.getGameChatTag(player);
    }

    public void setAllAnswered(){
        allAnswered = true;
        ((QuizGameScoreboard)getBoard()).stopQuestion();
        if(!hasNextQuestion()) {
            if(!announceWinner(false)) {
                PluginData.getMessageUtil().sendInfoMessage(getManager(),"There is no single winner. You can add more questions or announce multiple winners with /game winner");
            }
        }
        cancelTimerTask();
    }

    public List<Player> getConversationPlayers(){return inQuizConversation;}

    public boolean isInConversation(Player player){
        return inQuizConversation.contains(player);
    }

    public void removeQuizConversation(Player player){
        ((QuizGameScoreboard)getBoard()).playerFinished();
        inQuizConversation.remove(player);
    }

    public boolean allAnswered(){
        return inQuizConversation.isEmpty();
    }

    public void listQuestions(){
        if(questions.isEmpty()){
            PluginData.getMessageUtil().sendInfoMessage(getManager(),"No Questions in this game.");
            return;
        }
        PluginData.getMessageUtil().sendInfoMessage(getManager(),"Questions in this game:");
        int id = 1;
        for(AbstractQuestion question: questions){
            String questionText = question.getQuestion();
            String[] detailText = question.getDetails();
            String message = ChatColor.DARK_GREEN+String.valueOf(id)+ChatColor.AQUA+" ["+(question.getId()==0?"-":question.getId())+"]: "+ChatColor.WHITE+questionText;
            TextComponent text = new TextComponent(message);
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(Arrays.toString(detailText)).create()));
            getManager().sendMessage(text);
            id++;
        }
    }

    public void setRandom(boolean question, boolean choice) {
        if(this.randomQuestions && !question) {
            nextQuestion = 0;
        }
        this.randomQuestions = question;
        this.randomChoices = choice;
    }

    public void removeQuestion(int index) {
        questions.remove(index);
        ((QuizGameScoreboard)getBoard()).removeQuestion();
    }

    public void resetQuestions() {
        nextQuestion = 0;
        for(AbstractQuestion search: questions) {
            search.setAnswered(false);
        }
        ((QuizGameScoreboard)getBoard()).restart();
    }

    public boolean hasNextQuestion() {
        for(AbstractQuestion search: questions) {
            if(!search.isAnswered()){
                return true;
            }
        }
        return false;
    }

    public AbstractQuestion getNextQuestion() {
        if(hasNextQuestion()) {
            if(randomQuestions) {
                int questionsLeft = 0;
                for(AbstractQuestion search: questions) {
                    if(!search.isAnswered()){
                        questionsLeft++;
                    }
                }
                int rand = (int) Math.round(Math.floor(questionsLeft*Math.random()));
                nextQuestion = 0;
                while(questions.get(nextQuestion).isAnswered()) {
                    nextQuestion++;
                }
                for(int i=0; i<rand; i++) {
                    nextQuestion++;
                    while(questions.get(nextQuestion).isAnswered()) {
                        nextQuestion++;
                    }
                }
            }
            return questions.get(nextQuestion);
        }
        else {
            return null;
        }
    }

    public void setAnswerTime(int answerTime) {
        if(answerTime > 0) {
            this.answerTime = answerTime;
        }
    }

    public void sendQuestion() {
        if(hasNextQuestion()) {
            allAnswered = false;
            AbstractQuestion question = getNextQuestion();
            nextQuestion++;
            question.setAnswered(true);
            currentQuestion = question;
            ((QuizGameScoreboard)getBoard()).startQuestion(answerTime, getPlayers().size());
            AskQuestionConversationFactory askQuestionFactory
                    = new AskQuestionConversationFactory(MiniGamesPlugin.getPluginInstance(),answerTime);
            for (Player player : getPlayers()) {
                inQuizConversation.add(player);
                sendQuestionToPlayer(player,question);
                if(player.isConversing()) {
                    PluginData.getMessageUtil().sendErrorMessage(player, "Can't send the next quiz question to you as you are already in another conversation.");
                } else {
                    player.playEffect(player.getLocation(), Effect.CLICK1,0);
                    Conversation newConvo = askQuestionFactory.start(player, this, question);
                    playersInQuestion.put(player,newConvo);
                }
            }
            timerTask = MiniGamesPlugin.getInstance().getProxyServer().getScheduler().buildTask(MiniGamesPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    answerTime--;
                    if(answerTime < 1){
                        for(Player player: inQuizConversation){
                            if(currentQuestion instanceof NumberQuestion)
                                PluginData.getMessageUtil().sendInfoMessage(player,"Time to answer expired. Correct answer: "
                                        +question.getCorrectAnswer()+"."+" Allowed deviation from correct answer was "+((NumberQuestion)question).getPrecision()+".");
                            else
                                PluginData.getMessageUtil().sendInfoMessage(player,"Time to answer expired. Correct answer: " +currentQuestion.getCorrectAnswer());
                        }
                        cancelTimerTask();
                    }
                }
            }).delay(0,TimeUnit.SECONDS).repeat(1,TimeUnit.SECONDS).schedule();
        }
    }

    @Override
    public void addPlayer(Player player){
        super.addPlayer(player);
        scores.put(player,0);
    }

    @Override
    public void kickPlayer(Optional<Player> player){
        super.kickPlayer(player);
        removeQuizConversation(player);
        scores.remove(player);
        if(!allAnswered)
            setAllAnswered();
    }

    @Override
    public void removePlayer(Player player){
        super.removePlayer(player);
        removeQuizConversation(player);
        scores.remove(player);
        if(!allAnswered)
            setAllAnswered();
    }

    private void sendQuestionToPlayer(Player player, AbstractQuestion question){
        String questionText = question.getQuestion();
        String[] questionAnswer = null;
        if(question instanceof ChoiceQuestion){
            if(randomChoices)
                questionAnswer = ((ChoiceQuestion) question).getInRandomOrder();
            else
                questionAnswer = ((ChoiceQuestion) question).getInProperOrder();
        }
        String test = ChoiceQuestion.getAnswerCharacter();

        player.sendMessage(new ComponentBuilder(Style.HIGHLIGHT_STRESSED+"[Question] "+Style.HIGHLIGHT+questionText).create());
        if(questionAnswer != null){
            for(String answer: questionAnswer){
                player.sendMessage(new ComponentBuilder(answer).create());
                char answerIndex = answer.charAt(0);
                String answerTemp = answer.substring(1);
                player.sendMessage(new ComponentBuilder(Style.HIGHLIGHT+"["+answerIndex+"] "+Style.INFO+answerTemp).create());
            }
        }
        String hint = "";
        if(question instanceof SingleChoiceQuestion){
            hint = ChatColor.DARK_GREEN+"Type in chat the letter of the correct answer. \n"+ChatColor.GREEN+Style.BOLD+"Only one"+ChatColor.RESET+ChatColor.DARK_GREEN+" answer is correct.";
        }else if(question instanceof NumberQuestion){
            hint = ChatColor.DARK_GREEN+"Type in chat a whole number.";
        }else if(question instanceof FreeQuestion){
            hint = ChatColor.DARK_GREEN+"Type your answer in chat.";
        }else if(question instanceof ChoiceQuestion){
            hint = ChatColor.DARK_GREEN+"Type in the letters of the correct answers. \n"+ChatColor.LIGHT_PURPLE+Style.BOLD+"More than one"+ChatColor.RESET+ChatColor.DARK_GREEN+" answer may be correct.";
        }
        player.sendMessage(new ComponentBuilder(ChatColor.DARK_GREEN+"[Hint] "+hint).create());
    }

    private void cancelTimerTask(){
        inQuizConversation.clear();
        if(timerTask != null)
            timerTask.cancel();
    }

    public boolean announceWinner(boolean allowEqual) {
        int maxScore = 0;
        List<Player> winner = new ArrayList<>();
        boolean equalMaxScore = true;
        for(Player player: getPlayers()) {
            int score = scores.get(player);
            if(score>maxScore) {
                maxScore = score;
                winner.clear();
                winner.add(player);
                equalMaxScore = false;
            }
            else if(score == maxScore) {
                equalMaxScore = true;
                winner.add(player);
            }
        }
        if(winner.size()>0 && (allowEqual || winner.size()==1)) {
            String winnerNames = "";
            for(Player player: winner) {
                getWinHighscore().setQuizWin(player.getUniqueId());
                PluginData.getMessageUtil().sendInfoMessage(player,ChatColor.GOLD+"Congrats, You won the quiz game.");
                winnerNames = winner.get(0).getUsername();
                for(int i=1;i<winner.size()-1;i++) {
                    winnerNames = winnerNames + ", "+winner.get(i).getUsername();
                }
                if(winner.size()>1) {
                    winnerNames = winnerNames + " and "+winner.get(winner.size()-1).getUsername();
                }
            }
            notifyGame("Game Over, "+winnerNames+" won the quiz.");
            return true;
        }
        return false;
    }

    public void clearQuestions(){
        if(!allAnswered)
            setAllAnswered();
        for(Player player: scores.keySet())
            scores.replace(player,0);
        questions.clear();
        ((QuizGameScoreboard)getBoard()).clearQuestions();
        resetQuestions();
        PluginData.getMessageUtil().sendInfoMessage(getManager(),"You removed all questions from this Lore Quiz.");
    }

    public void incrementScore(Player player) {
        scores.replace(player,scores.get(player)+1);
        ((QuizGameScoreboard)getBoard()).score(player);
    }

    public int[] loadQuestionsFromDataFile(File file, String quizCategories,
                                           boolean matchAllCategories, int maxNumber) throws FileNotFoundException {
        List<AbstractQuestion> newQuestions = new ArrayList<>();
        boolean loadAll = quizCategories.equalsIgnoreCase("all");
        int found=0;
        try {
            int line = 0;
            try (Scanner reader = new Scanner(file, StandardCharsets.UTF_8.name())) {
                while(reader.hasNext()){
                    try {
                        line++;
                        StringTokenizer tokenizer = new StringTokenizer(reader.nextLine(),";");
                        String questionCategories = tokenizer.nextToken();
                        if(loadAll || isQuestionInQuizCategories(quizCategories, questionCategories, matchAllCategories)) {
                            AbstractQuestion newQuestion = questionFromString(tokenizer, questionCategories);
                            newQuestion.setId(line);
                            newQuestions.add(newQuestion);
                        }
                    } catch (ParseException | NoSuchElementException ex) {
                        Logger.getLogger(QuizGame.class.getName()).log(Level.SEVERE,
                                "Error reading questions from data file in line "+line
                                        +". Question skipped. ");
                    }
                }
                found=newQuestions.size();
                while(newQuestions.size()>maxNumber) {
                    int random = new Double(Math.floor(Math.random()*newQuestions.size())).intValue();
                    int random = NumericUtil.getRandom(0, newQuestions.size()-1);
                    if(random >= newQuestions.size()) {
                        random = newQuestions.size()-1;
                    }
                    newQuestions.remove(random);
                }
                for(AbstractQuestion question: newQuestions) {
                    addQuestion(question,-1);
                }
                ((QuizGameScoreboard)getBoard()).updateQuiz(questions.size());
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getInstance().getLogger().error(null, ex);
            throw ex;
        }
        return new int[]{found,newQuestions.size()};
    }

    public void addQuestion(AbstractQuestion question, int index) {
        if(index==-1) {
            questions.add(question);
        }
        else {
            questions.add(index, question);
            if(nextQuestion>=index) {
                nextQuestion++;
            }
        }
        ((QuizGameScoreboard)getBoard()).addQuestion();
    }

    public void saveQuestionsToJson(File file, String description) throws IOException {
        saveQuestionsToJson(file, description, questions);
    }

    public static void saveQuestionsToJson(File file, String description, List<AbstractQuestion> questions)
            throws IOException {
        JSONArray jQuestionArray = new JSONArray();
        for (AbstractQuestion question : questions) {
            JSONObject jQuestion = new JSONObject();
            jQuestion.put("Question",question.getQuestion());
            jQuestion.put("Type", question.getType().getName());
            jQuestion.put("Categories", question.getCategories());
            switch(question.getType()) {
                case FREE:
                    jQuestion.put("Answer", ((FreeQuestion)question).getAnswer());
                    break;
                case NUMBER:
                    jQuestion.put("Answer", ((NumberQuestion)question).getAnswer());
                    jQuestion.put("Precision", ((NumberQuestion)question).getPrecision());
                    break;
                case SINGLE:
                case MULTI:
                    JSONArray jChoices = new JSONArray();
                    jChoices.addAll(Arrays.asList(((ChoiceQuestion)question).getAnswers()));
                    jQuestion.put("Choices", jChoices);
                    jQuestion.put("Correct", ((ChoiceQuestion)question).getCorrectAnswer());
            }
            jQuestionArray.add(jQuestion);
        }
        JSONObject jFile = new JSONObject();
        jFile.put("questions", jQuestionArray);
        jFile.put("description", description);
        try(OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
            jFile.writeJSONString(fw);
        }
    }




    public void loadQuestionsFromJson(File file) throws FileNotFoundException, ParseException{
        List<AbstractQuestion> newQuestions = new ArrayList<>();
        loadQuestionsFromJson(file, newQuestions);
        for(AbstractQuestion question : newQuestions) {
            addQuestion(question, -1);
        }
    }

    public static void loadQuestionsFromJson(File file, List<AbstractQuestion> questions)
            throws FileNotFoundException, ParseException {
        try {
            String input;
            try (Scanner reader = new Scanner(file, StandardCharsets.UTF_8.name())) {
                input = "";
                while(reader.hasNext()){
                    input = input+reader.nextLine();
                }
            }
            JSONObject jInput = (JSONObject) new JSONParser().parse(input);
            JSONArray jQuestions = (JSONArray) jInput.get("questions");
            for (Object questionObject : jQuestions) {
                JSONObject jQuestion = (JSONObject) questionObject;
                QuestionType type = QuestionType.getQuestionType((String) jQuestion.get("Type"));
                AbstractQuestion newQuestion;
                switch(type) {
                    case FREE:
                        newQuestion = new FreeQuestion((String) jQuestion.get("Question"),
                                (String) jQuestion.get("Answer"),
                                (String) jQuestion.get("Categories"));
                        break;
                    case NUMBER:
                        newQuestion = new NumberQuestion((String) jQuestion.get("Question"),
                                ((Long) jQuestion.get("Answer")).intValue(),
                                ((Long) jQuestion.get("Precision")).intValue(),
                                (String) jQuestion.get("Categories"));
                        break;
                    case MULTI:
                        newQuestion = new ChoiceQuestion((String) jQuestion.get("Question"),
                                readStringArray(jQuestion,"Choices"),
                                (String) jQuestion.get("Correct"),
                                (String) jQuestion.get("Categories"));
                        break;
                    case SINGLE:
                        newQuestion = new SingleChoiceQuestion((String) jQuestion.get("Question"),
                                readStringArray(jQuestion,"Choices"),
                                (String) jQuestion.get("Correct"),
                                (String) jQuestion.get("Categories"));
                        break;
                    default:
                        throw new ParseException(org.json.simple.parser.ParseException.ERROR_UNEXPECTED_TOKEN);
                }
                questions.add(newQuestion);
            }
        } catch (FileNotFoundException | ParseException ex) {
            MiniGamesPlugin.getInstance().getLogger().error(null, ex);
            throw ex;
        }
    }




    private static String[] readStringArray(JSONObject jQuestion, String key) {
        JSONArray jAnswers = (JSONArray) jQuestion.get(key);
        List<String> answers= new ArrayList<>();
        for(Object answerObject : jAnswers) {
            answers.add((String) answerObject);
        }
        return answers.toArray(new String[0]);
    }

    private int getQuestionTypeNumber(QuestionType type) {
        return switch (type) {
            case FREE -> 1;
            case NUMBER -> 2;
            case SINGLE -> 3;
            case MULTI -> 4;
            default -> 1;
        };
    }

    private String questionToString(AbstractQuestion question) {
        String line = question.getCategories()+";"+
                +getQuestionTypeNumber(question.getType())+";"
                +question.getQuestion()+";";
        line = switch (question.getType()) {
            case FREE -> line + question.getCorrectAnswer();
            case NUMBER -> line + question.getCorrectAnswer() + ";" + ((NumberQuestion) question).getPrecision();
            case SINGLE, MULTI -> {
                for (String choice : ((ChoiceQuestion) question).getAnswers()) {
                    line = line + choice + ";";
                }
                yield line + ((ChoiceQuestion) question).getCorrectAnswer();
            }
        };
        return line;
    }

    public void saveQuestionsToDataFile(File file) throws FileNotFoundException, IOException {
        for(AbstractQuestion question: questions) {
            if(question.getId()!=0) {
                storeQuestionsToDataFile(file);
                return;
            }
        }
        addQuestionsToDataFile(file);
    }

    private void addQuestionsToDataFile(File file) throws FileNotFoundException, IOException {
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(fw)) {
            for(AbstractQuestion question: questions) {
                writer.println(questionToString(question));
            }
        }
    }

    private void storeQuestionsToDataFile(File file) throws FileNotFoundException, IOException {
        List<AbstractQuestion> saveQuestions = new ArrayList<>();
        saveQuestions.addAll(questions);
        Comparator<AbstractQuestion> comp = new Comparator<AbstractQuestion>(){
            @Override
            public int compare(AbstractQuestion o1, AbstractQuestion o2) {
                if(o1.getId()==0) return 1;
                if(o2.getId()==0) return -1;
                if(o1.getId()==o2.getId()) return 0;
                return (o1.getId()<o2.getId()?-1:1);
            }
        };
        Collections.sort(saveQuestions, comp);
        File tmpFile = new File(file.toString()+".tmp");
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(tmpFile, true), StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(fw);
             Scanner reader = new Scanner(file, StandardCharsets.UTF_8.name())) {
            int line = 1;
            for(AbstractQuestion question: saveQuestions) {
                int id = question.getId();
                if(id==0) {
                    while(reader.hasNext()) {
                        writer.println(reader.nextLine());
                    }
                    writer.println(questionToString(question));
                } else {
                    while(line<id) {
                        writer.println(reader.nextLine());
                        line++;
                    }
                    reader.nextLine();
                    String str = questionToString(question);
                    writer.println(str);
                    line++;
                }
            }
            while(reader.hasNext()) {
                String str =reader.nextLine();
                writer.println(str);
            }
        }
        file.delete();
        tmpFile.renameTo(file);
    }

    public int[] loadQuestionsFromDataFile(File file, List<Integer> questionIds) throws FileNotFoundException {
        Collections.sort(questionIds);
        int found = 0;
        try {
            int line = 1;
            try (Scanner reader = new Scanner(file, StandardCharsets.UTF_8.name())) {
                for(Integer questionId: questionIds) {
                    try {
                        while(line<questionId && reader.hasNext()) {
                            reader.nextLine();
                            line++;
                        }
                        if(reader.hasNext()) {
                            StringTokenizer tokenizer = new StringTokenizer(reader.nextLine(),";");
                            String questionCategories = tokenizer.nextToken();
                            AbstractQuestion question = questionFromString(tokenizer, questionCategories);
                            question.setId(line);
                            addQuestion(question,-1);
                            found++;
                            line++;
                        }
                    } catch (ParseException | NoSuchElementException ex) {
                        Logger.getLogger(QuizGame.class.getName()).log(Level.SEVERE,
                                "Error reading questions from data file in line "+line
                                        +". Question skipped. ");
                        line++;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getInstance().getLogger().error(null, ex);
            throw ex;
        }
        return new int[]{found,found};

    }

    private AbstractQuestion questionFromString(StringTokenizer tokenizer, String questionCategories) throws ParseException {
        QuestionType type = getQuestionType(StringUtil.parseInt(tokenizer.nextToken()));
        String question = tokenizer.nextToken();
        String[] choices = new String[]{"","","",""};
        if(type.equals(QuestionType.MULTI) || type.equals(QuestionType.SINGLE)) {
            for(int i = 0; i<4; i++) {
                choices[i] = tokenizer.nextToken();
            }
        }
        String answer = tokenizer.nextToken();
        AbstractQuestion newQuestion;
        switch(type) {
            case FREE:
                newQuestion = new FreeQuestion(question,answer,questionCategories);
                break;
            case NUMBER:
                int precision = StringUtil.parseInt(tokenizer.nextToken());
                int answerInt = StringUtil.parseInt(answer);
                newQuestion = new NumberQuestion(question,
                        answerInt,
                        precision,questionCategories);
                break;
            case MULTI:
                newQuestion = new ChoiceQuestion(question,
                        choices,
                        answer,questionCategories);
                break;
            case SINGLE:
                newQuestion = new SingleChoiceQuestion(question,
                        choices,
                        answer,questionCategories);
                break;
            default:
                throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
        }
        return newQuestion;
    }

    private QuestionType getQuestionType(int index) {
        switch(index) {
            case 1: return QuestionType.FREE;
            case 2: return QuestionType.NUMBER;
            case 3: return QuestionType.SINGLE;
            case 4: return QuestionType.MULTI;
        }
        return QuestionType.FREE;
    }

    private boolean isQuestionInQuizCategories(String quizCategories,
                                               String questionCategories,
                                               boolean matchAllCategories) {
        String wantedCategories= "";
        String excludedCategories="";
        boolean exclude = false;
        for(char character: quizCategories.toCharArray()) {
            if(character=='-') {
                exclude = true;
            } else {
                if(exclude) {
                    excludedCategories+=character;
                } else {
                    wantedCategories+=character;
                }
                exclude = false;
            }
        }
        if(checkWantedCategories(wantedCategories, questionCategories, matchAllCategories)) {
            return checkExcludedCategories(excludedCategories, questionCategories);
        } else {
            return false;
        }
    }

    public void QuizGameWinner(Player player){
        if(!announceWinner(true)){
            PluginData.getMessageUtil().sendErrorMessage(player,"There is no winner.");
        }
    }

    private boolean checkWantedCategories(String wantedQuizCategories,
                                          String questionCategories,
                                          boolean matchAllCategories) {
        for(char character: wantedQuizCategories.toCharArray()) {
            if(matchAllCategories) {
                if(questionCategories.indexOf(character)<0) {
                    return false;
                }
            } else {
                if(questionCategories.indexOf(character)>=0) {
                    return true;
                }
            }
        }
        return matchAllCategories;
    }

    private boolean checkExcludedCategories(String excludedCategories,
                                            String questionCategories) {
        for(char character: excludedCategories.toCharArray()) {
            if(questionCategories.indexOf(character)>=0) {
                return false;
            }
        }
        return true;
    }

    public boolean isRandomChoices() {
        return randomChoices;
    }

    public AbstractQuestion getCurrentQuestion(){
        return currentQuestion;
    }
}