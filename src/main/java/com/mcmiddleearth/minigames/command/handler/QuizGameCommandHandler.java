package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quiz.QuizShowCategories;
import com.mcmiddleearth.minigames.util.Permission;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;

import java.io.FileNotFoundException;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;


/**
 * @author Jubo
 */
public class QuizGameCommandHandler {

    private final GameType type = GameType.LORE_QUIZ;

    public QuizGameCommandHandler(){}

    public HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder){
        helpfulLiteralBuilder
                .then(HelpfulLiteralBuilder.literal("acceptquestions")
                        .withHelpText("")
                        .withTooltip("")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.STAFF))
                        .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                        .then(HelpfulRequiredArgumentBuilder.argument("description",greedyString())
                        .executes(context -> doCommand(context.getSource(), "acceptquestions",context.getArgument("filename",String.class),context.getArgument("description",String.class))))))
                .then(HelpfulLiteralBuilder.literal("clear")
                        .withHelpText("Removes all questions from a quiz game.")
                        .withTooltip("Removes all questions from a quiz game.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(),"clear",null)))
                .then(HelpfulLiteralBuilder.literal("load")
                        .withHelpText("Loads questions from a quiz data file.")
                        .withTooltip("Loads all questions from the file <filename>. The questions will be appended to the existing questions.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender,type) && PluginData.isManager(sender)&& !PluginData.isAlreadyAnnounced(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                .executes(context -> doCommand(context.getSource(),"load",context.getArgument("filename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("loadquestions")
                        .withHelpText("Loads questions from the question table.")
                        .withTooltip("Gives you 15 questions of your wanted category.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isCorrectGameType(sender, type) && PluginData.isManager(sender) && !PluginData.isAlreadyAnnounced(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("category",word())
                                .executes(context -> doCommand(context.getSource(),"loadquestions",context.getArgument("category",String.class)))))
                .then(HelpfulLiteralBuilder.literal("showcategories")
                        .withHelpText("Shows all available question categories.")
                        .withTooltip("Shows all available question categories.")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER))
                        .executes(context -> doCommand(context.getSource(), "showcategories",null)));

        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        QuizGame quizgame;
        switch (command){
            case "showcategories":
                QuizShowCategories.execute(sender);
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
