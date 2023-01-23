package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.command.argument.CommandQuestionTypeArgument;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quiz.QuizShowCategories;
import com.mcmiddleearth.minigames.util.Permission;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.StringUtil;
import com.mcmiddleearth.minigames.util.Style;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;


/**
 * @author Jubo
 */
public class QuizGameCommandHandler {

    /*
    TODO:
     test clear questions
     test winner
     test stat
     test load and save quiz
     figure confirmation listener out, also really important for submitting questions and stuff
     create the accept conversation
     test delete
     test acceptquestion
     */

    private final GameType type = GameType.LORE_QUIZ;

    public QuizGameCommandHandler(){}

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){
        helpfulLiteralBuilder
                .then(HelpfulLiteralBuilder.literal("acceptquestions")
                        .withHelpText("Saves questions to file.")
                        .withTooltip("Saves all questions of the game to file <filename>. A <description> will be saved with the questions.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.STAFF))
                        .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                .then(HelpfulRequiredArgumentBuilder.argument("description",greedyString())
                                        .executes(context -> doCommand(context.getSource(), "acceptquestions",context.getArgument("filename",String.class),context.getArgument("description",String.class))))))
                .then(HelpfulLiteralBuilder.literal("clear")
                        .withHelpText("Removes all questions from a quiz game.")
                        .withTooltip("Removes all questions from a quiz game.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(),"clear",null)))
                .then(HelpfulLiteralBuilder.literal("loadquiz")
                        .withHelpText("Loads questions from a quiz data file.")
                        .withTooltip("Loads all questions from the file <filename>. The questions will be appended to the existing questions.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender)&& !PluginData.isAlreadyAnnounced(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                .executes(context -> doCommand(context.getSource(),"loadquiz",context.getArgument("filename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("loadquestions")
                        .withHelpText("Loads questions from the question table.")
                        .withTooltip("Gives you 15 questions of your wanted category.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender, type) && PluginData.isManager(sender) && !PluginData.isAlreadyAnnounced(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("category",word())
                                .executes(context -> doCommand(context.getSource(),"loadquestions",context.getArgument("category",String.class)))))
                .then(HelpfulLiteralBuilder.literal("question")
                        .withHelpText("Manipulates questions of a quiz game.")
                        .withTooltip("Arguments <questionType> may be 'single', 'multi', 'free' or 'number' and will initiate a conversation to create a new question, which will be added to the quiz. " +
                                "Argument <manage> may be 'remove', 'list', 'edit', 'submit', 'review' ,'accept', 'clear' or 'load'. See manual for full description.")
                        .then(HelpfulLiteralBuilder.literal("accept")
                                .requires(sender -> PluginData.hasPermission(sender, Permission.STAFF))
                                .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                        .then(HelpfulRequiredArgumentBuilder.argument("description",greedyString())
                                                .executes(context -> doCommand(context.getSource(), "acceptquestions",context.getArgument("filename",String.class),context.getArgument("description",String.class))))))
                        .then(HelpfulLiteralBuilder.literal("clear")
                                .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                                .executes(context -> doCommand(context.getSource(),"clear",null)))
                        .then(HelpfulLiteralBuilder.literal("edit")
                                .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                                .then(HelpfulRequiredArgumentBuilder.argument("index",integer())
                                        .executes(context -> doCommand(context.getSource(), "editquestion", String.valueOf(context.getArgument("index",Integer.class))))))
                        .then(HelpfulLiteralBuilder.literal("list")
                                .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender)).requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                                .then(HelpfulRequiredArgumentBuilder.argument("page",integer())
                                        .executes(context -> doCommand(context.getSource(), "listquestions", String.valueOf(context.getArgument("page",Integer.class))))))
                        .then(HelpfulLiteralBuilder.literal("load")
                                .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender)&& !PluginData.isAlreadyAnnounced(sender))
                                .then(HelpfulRequiredArgumentBuilder.argument("category",word())
                                        .executes(context -> doCommand(context.getSource(),"load",context.getArgument("category",String.class)))))
                        .then(HelpfulLiteralBuilder.literal("remove")
                                .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                                .then(HelpfulRequiredArgumentBuilder.argument("index",integer())
                                        .executes(context -> doCommand(context.getSource(), "removequestion", String.valueOf(context.getArgument("index",Integer.class))))))
                        .then(HelpfulLiteralBuilder.literal("review")
                                .requires(sender -> PluginData.hasPermission(sender,Permission.STAFF) && !PluginData.isManager(sender))
                                .executes(context -> doCommand(context.getSource(), "reviewquestions",null))
                                .then(HelpfulRequiredArgumentBuilder.argument("check",word())
                                    .executes(context -> doCommand(context.getSource(), "reviewquestions",context.getArgument("check",String.class)))))
                        .then(HelpfulLiteralBuilder.literal("submit")
                                .requires(sender -> PluginData.hasPermission(sender,Permission.USER))
                                .then(HelpfulRequiredArgumentBuilder.argument("questiontype",new CommandQuestionTypeArgument())
                                        .executes(context -> doCommand(context.getSource(), "submitquestion",context.getArgument("questiontype",String.class))))))
                .then(HelpfulLiteralBuilder.literal("random")
                        .withHelpText("Defines the order of questions.")
                        .withTooltip("'off' will set all questions and choices to be shown in saved order. 'questions' will show questions in random order. 'choices' will show possible" +
                                " answers for a question in random order. 'all' or just no argument will show questions and choices in random order")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("off|questions|choices|all",word())
                                .executes(context -> doCommand(context.getSource(), "random",context.getArgument("off|questions|choices|all",String.class)))))
                .then(HelpfulLiteralBuilder.literal("reviewquestions")
                        .withHelpText("Creates a quiz game with submitted questions.")
                        .withTooltip("Creates a lore quiz game with all submitted question which have not been reviewed before. The quiz is save in a quiz file with filename <rYYYY_MM_DD> you can then " +
                                "edit or remove questions and accept (save) them for the MCME lore question table.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.STAFF) && !PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(), "reviewquestions",null))
                        .then(HelpfulRequiredArgumentBuilder.argument("check",word())
                                .executes(context -> doCommand(context.getSource(), "reviewquestions",context.getArgument("check",String.class)))))
                .then(HelpfulLiteralBuilder.literal("savequiz")
                        .withHelpText("Saves questions to file.")
                        .withTooltip("Saves all questions of the game to file <filename>. A <description> will be saved with the questions.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                .then(HelpfulRequiredArgumentBuilder.argument("description",greedyString())
                                        .executes(context -> doCommand(context.getSource(), "savequiz",context.getArgument("filename",String.class),context.getArgument("description",String.class))))))
                .then(HelpfulLiteralBuilder.literal("send")
                        .withHelpText("Sends the next question.")
                        .withTooltip("Sends the next question to all participating players and the game manager. Without a given [answerTime] players will have 30 sec to answer. A specified [answerTime] will be use " +
                                "for all later questions too.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(), "send",null))
                        .then(HelpfulRequiredArgumentBuilder.argument("answerTime",integer())
                                .executes(context -> doCommand(context.getSource(), "send", String.valueOf(context.getArgument("answerTime",Integer.class))))))
                .then(HelpfulLiteralBuilder.literal("showcategories")
                        .withHelpText("Shows all available question categories.")
                        .withTooltip("Shows all available question categories.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER))
                        .executes(context -> doCommand(context.getSource(), "showcategories",null)))
                .then(HelpfulLiteralBuilder.literal("stat")
                        .withHelpText("Shows players in question conversation.")
                        .withTooltip("Shows all players who are still in the conversation to answer a question. They may have made an invalid input and aren't aware that they are still in the conversation.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(), "stat",null)))
                .then(HelpfulLiteralBuilder.literal("submitquestion")
                        .withHelpText("Submits a quiz question.")
                        .withTooltip("single|multi|free|number: Initiates a conversation to create a new question of the specified type. Without a type a single choice question is created.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER))
                        .then(HelpfulRequiredArgumentBuilder.argument("questiontype",new CommandQuestionTypeArgument())
                                .executes(context -> doCommand(context.getSource(), "submitquestion",context.getArgument("questiontype",String.class)))));


        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        QuizGame quizgame;
        File file;
        switch (command){
            case "acceptquestions":
                AbstractGame.addConversation((ProxiedPlayer) ((MinigameCommandSender)sender).getCommandSender());
                quizgame = (QuizGame) PluginData.getGame(sender);
                quizgame.setAcceptConversation(true);
                break;
            case "clear":
                quizgame = (QuizGame) PluginData.getGame(sender);
                quizgame.clearQuestions();
                break;
            case "editquestion":
                sendNotImplementedYetMessage(sender);
                break;
            case "listquestions":
                sendNotImplementedYetMessage(sender);
                break;
            case "loadquiz":
                quizgame = (QuizGame) PluginData.getGame(sender);
                file = new File(PluginData.getQuestionDir(),args[0]+".json");
                try{
                    quizgame.loadQuestionsFromJson(file);
                    PluginData.getMessageUtil().sendInfoMessage(sender,"Questions loaded from file.");
                }catch (FileNotFoundException ex) {
                    PluginData.getMessageUtil().sendErrorMessage(sender,"File not found.");
                } catch (ParseException ex) {
                    PluginData.getMessageUtil().sendErrorMessage(sender,"The file contains invalid data.");
                }
                break;
            case "loadquestions":
                quizgame = (QuizGame) PluginData.getGame(sender);
                try {
                    int[] result =  quizgame.loadQuestionsFromDataFile(PluginData.getQuestionDataTable(),args[0],false,15);
                    sendQuestionsLoadedMessage(sender,result,15);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "random":
                quizgame = (QuizGame) PluginData.getGame(sender);
                switch (args[0]){
                    case "off":
                        quizgame.setRandom(false,false);
                        PluginData.getMessageUtil().sendInfoMessage(sender,"Questions and choices will be presented in proper order.");
                        break;
                    case "questions":
                        quizgame.setRandom(true,false);
                        PluginData.getMessageUtil().sendInfoMessage(sender,"Questions will be sended in random order.");
                        break;
                    case "choices":
                        quizgame.setRandom(false,true);
                        PluginData.getMessageUtil().sendInfoMessage(sender,"Choices will be presented in random order.");
                        break;
                    case "all":
                        quizgame.setRandom(true,true);
                        PluginData.getMessageUtil().sendInfoMessage(sender,"Questions and Choises will be presented in random order.");
                        break;
                    default:
                        PluginData.getMessageUtil().sendErrorMessage(sender,"Error: Try off|questions|choices|all");
                        break;
                }
                break;
            case "removequestion":
                sendNotImplementedYetMessage(sender);
                break;
            case "reviewquestions":
                sendNotImplementedYetMessage(sender);
                break;
            case "savequiz":
                quizgame = (QuizGame) PluginData.getGame(sender);
                file = new File(PluginData.getQuestionDir(),args[0]+".json");
                String description = args[1];
                if(file.exists()){
                    AbstractGame.addConversation((ProxiedPlayer) ((MinigameCommandSender)sender).getCommandSender());
                    quizgame.setSaveInfos(file,description);
                    PluginData.getMessageUtil().sendInfoMessage(sender, "A question file with that name already exists. Overwrite it?");
                } else {
                    try{
                        quizgame.saveQuestionsToJson(file,description);
                        PluginData.getMessageUtil().sendInfoMessage(sender,"Questions of the game were saved to disk.");
                    }catch (IOException ex){
                        PluginData.getMessageUtil().sendErrorMessage(sender,"There was an error. Nothing was saved.");
                        Logger.getLogger(QuizGameCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case "send":
                quizgame = (QuizGame) PluginData.getGame(sender);
                if(args != null){
                    quizgame.setAnswerTime(StringUtil.parseInt(args[0]));
                }else{
                    quizgame.setAnswerTime(30);
                }
                quizgame.sendQuestion();
                break;
            case "showcategories":
                QuizShowCategories.execute(sender);
                break;
            case "stat":
                quizgame = (QuizGame) PluginData.getGame(sender);
                if(quizgame.allAnswered()){
                    PluginData.getMessageUtil().sendInfoMessage(sender, ChatColor.RED+"NO QUESTION running."+ChatColor.AQUA+" Online players:");
                    PluginData.getMessageUtil().sendInfoMessage(sender, String.valueOf(quizgame.getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet())));
                }else{
                    PluginData.getMessageUtil().sendInfoMessage(sender, "Players in question conversation:");
                    PluginData.getMessageUtil().sendInfoMessage(sender, String.valueOf(quizgame.getConversationPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet())));
                }
                break;
            case "submitquestion":
                sendNotImplementedYetMessage(sender);
                break;
            default:
                sendNothereMessage(sender);
        }
        return 0;
    }

    private void sendNothereMessage(McmeCommandSender sender){
        PluginData.getMessageUtil().sendErrorMessage(sender,"You shouldn't be here. Try Again!");
    }

    private void sendNotImplementedYetMessage(McmeCommandSender sender){
        PluginData.getMessageUtil().sendErrorMessage(sender,"This is not yet implemented.");
    }

    private void sendQuestionsLoadedMessage(McmeCommandSender cs, int[] result, int maxNumber) {
        if(result[0]==0) {
            PluginData.getMessageUtil().sendErrorMessage(cs, "Sorry, no question found matching your query.");
        } else if(result[0]<10 && result[0]<maxNumber) {
            PluginData.getMessageUtil().sendInfoMessage(cs, Style.HIGHLIGHT+"Warning!"+Style.INFO
                    +" Only "+Style.STRESSED+result[0]+Style.INFO                                           +" questions were found matching your query.");
        } else if(result[0]>result[1]) {
            PluginData.getMessageUtil().sendInfoMessage(cs, "Found "+Style.STRESSED+result[0]+Style.INFO
                    +" Questions. "+Style.STRESSED+result[1]+Style.INFO
                    +" questions loaded.");
        } else if(result[0]<maxNumber) {
            PluginData.getMessageUtil().sendInfoMessage(cs, "Only "+Style.STRESSED+result[0]+Style.INFO
                    +" questions found and loaded.");
        } else {
            PluginData.getMessageUtil().sendInfoMessage(cs, ""+Style.STRESSED+result[0]+Style.INFO
                    +" questions loaded from MCME question table.");
        }
    }
}
