package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.command.argument.CommandGameTypeArgument;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quiz.QuizShowCategories;
import com.mcmiddleearth.minigames.scoreboard.QuizGameScoreboard;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

import java.io.FileNotFoundException;

/**
 * @author Jubo
 */
public class GameCommandHandler extends AbstractCommandHandler {

    private static final QuizGameScoreboard board = new QuizGameScoreboard();

    public GameCommandHandler(String name){
        super(name);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder) {
        helpfulLiteralBuilder
                .requires(sender -> (sender instanceof MinigameCommandSender))
                .then(HelpfulLiteralBuilder.literal("showcategories")
                        .withHelpText("")
                        .withTooltip("")
                        .requires(sender -> true)
                        .executes(context -> doQuizCommand(context.getSource(), "showcategories",null)))
                .then(HelpfulLiteralBuilder.literal("create")
                        .withTooltip("")
                        .withHelpText("")
                        .then(HelpfulRequiredArgumentBuilder.argument("gametype",new CommandGameTypeArgument())
                        .then(HelpfulRequiredArgumentBuilder.argument("name",word())
                        .executes(context -> doMainCommand(context.getSource(), "create",context.getArgument("gametype",String.class),context.getArgument("name",String.class))))))
                .then(HelpfulLiteralBuilder.literal("loadquestions")
                        .withHelpText("")
                        .withTooltip("")
                        .then(HelpfulRequiredArgumentBuilder.argument("category",word())
                        .executes(context -> doQuizCommand(context.getSource(),"loadquestions",context.getArgument("category",String.class)))));

        /*
                .then(HelpfulLiteralBuilder.literal("test")
                .executes(context -> doCommand(context.getSource(),"test",null)))
                .then(HelpfulLiteralBuilder.literal("test2")
                        .executes(context -> doCommand(context.getSource(),"test2",null)))
                .then(HelpfulLiteralBuilder.literal("test3")
                        .executes(context -> doCommand(context.getSource(), "test3",null)));

         */
        return helpfulLiteralBuilder;
    }

    private int doMainCommand(McmeCommandSender sender, String command, String... args){
        AbstractGame game;
        switch (command){
            case "create":
                GameType type = GameType.getGameType(args[0]);
                switch (type){
                    case LORE_QUIZ:
                        game = new QuizGame((ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender(),args[1]);
                        PluginData.addGame(game);
                        PluginData.getMessageUtil().sendInfoMessage(sender,"The quiz game was created.");
                        break;
                }
                break;
        }
        return 0;
    }

    private int doQuizCommand(McmeCommandSender sender, String command, String... args){
        QuizGame quizgame;
        switch (command){
            case "showcategories":
                QuizShowCategories.execute(sender);
                break;
            case "loadquestions":
                quizgame = (QuizGame) PluginData.getGame((MinigameCommandSender) sender);
                try {
                    int[] result =  quizgame.loadQuestionsFromDataFile(PluginData.getQuestionDataTable(),args[0],false,15);
                    sendQuestionsLoadedMessage(sender,result,15);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
        return 0;
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
