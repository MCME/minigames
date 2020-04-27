/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.data;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.confirmation.ConfirmationFactory;
import com.mcmiddleearth.minigames.conversation.quiz.CreateQuestionConversationFactory;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    private final static MessageUtil messageUtil = new MessageUtil();
    private static CreateQuestionConversationFactory createQuestionFactory;
    private static ConfirmationFactory confirmationFactory;
    
    private static final List<OfflinePlayer> noGameChat = new ArrayList<>();
    private static final List<AbstractGame> games = new ArrayList<>();
    
    private static final File questionDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
                                                    + File.separator + "QuizQuestions");
    private static final File questionDataTable = new File(questionDir,"questionTable.dat");
    private static final File submittedQuestionsFile = new File(questionDir,"submitted.json");
    static final QuizGame questionSubmitGame = new QuizGame(null, "submitQuestions");
    private static final File questionCategoriesFile = new File(questionDir,"questionCategories.dat");
    private static final List<String> questionCategories = new ArrayList<>();
    private static final File raceDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
                                                    + File.separator + "Races");
    private static final File golfDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Courses");
    private static final File pvpDirectory = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Arenas");
    private static final File loadoutDirectory = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Loadouts");

    public static boolean pvpRunning = false;
    
    static {
        if(!MiniGamesPlugin.getPluginInstance().getDataFolder().exists()) {
            MiniGamesPlugin.getPluginInstance().getDataFolder().mkdirs();
        }

        if(!questionDir.exists()) {
            questionDir.mkdirs();
        }

        if(!raceDir.exists()) {
            raceDir.mkdirs();
        }

        if(!golfDir.exists()) {
            golfDir.mkdirs();
        }

        if(!pvpDirectory.exists()) {
            pvpDirectory.mkdirs();
        }

        if(!loadoutDirectory.exists()) {
            loadoutDirectory.mkdirs();
        }
    }
   
    public static void createConversationFactories() {
        createQuestionFactory = new CreateQuestionConversationFactory(MiniGamesPlugin.getPluginInstance());
        confirmationFactory = new ConfirmationFactory(MiniGamesPlugin.getPluginInstance());
    }
    
    public static AbstractGame getGame(Player player) {
        for(AbstractGame game : games) {
            if(PlayerUtil.isSame(game.getManager(),player)) {
                return game;
            }
            if(game.getPlayers().contains(player.getUniqueId())) {
                return game;
            }
        }
        return null;
    }
    
    public static AbstractGame getGame(String name) {
        for(AbstractGame game : games) {
            if(game.getName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }
    
    public static boolean gameRunning() {
        for(AbstractGame game : games) {
            if(game.isAnnounced()) {
                return true;
            }
        }
        return false;
    }
    
    public static void addGame(AbstractGame game) {
        games.add(game);
    }
    
    public static void removeGame(AbstractGame game) {
        games.remove(game);
    }
    
    public static boolean isInGame(Player player) {
        for(AbstractGame game : games) {
            return game.isInGame(player);
        }
        return false;
    }
    
    public static boolean isManager(Player player) {
        for(AbstractGame game : games) {
            if(PlayerUtil.isSame(game.getManager(),player)) {
                return true;
            }
        }
        return false;
    }

    public static void setGameChat(Player player, boolean b) {
        if(!b) {
            noGameChat.add(player);
        }
        else {
            noGameChat.remove(player);
        }
    }

    public static boolean getGameChat(OfflinePlayer player) {
        for(OfflinePlayer search: noGameChat) {
            if(PlayerUtil.isSame(search,player)) {
                return false;
            }
        }
       return true;
    }
    
    public static void stopSpectating(Player player) {
        for(AbstractGame game: games) {
            if(game.isSpectating(player)) {
                GameChatUtil.sendAllInfoMessage(player, game, player.getName()+" stopped spectating.");
                PluginData.getMessageUtil().sendInfoMessage(player, "You stopped spectating.");
                game.removeSpectator(player);
                return;
            }
        }
    }
    
    public static boolean isSpectating(Player player) {
        for(AbstractGame game: games) {
            if(game.isSpectating(player)) {
                return true;
            }
        }
        return false;
    }
    
    public static void cleanup() {
        Checkpoint.cleanup();
    }
    
    public static void load() {
        try {
            try (Scanner reader = new Scanner(questionCategoriesFile, StandardCharsets.UTF_8.name())) {
                questionCategories.clear();
                if(reader.hasNext()) {
                    reader.nextLine();
                }
                while(reader.hasNext()){
                    questionCategories.add(reader.nextLine());
                }
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
        }
        try {
            questionSubmitGame.loadQuestionsFromJson(submittedQuestionsFile);
        } catch (FileNotFoundException | ParseException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.INFO, "No submitted questions found.");
        }
    }
    
    public static boolean areValidCategories(String categories) {
        for(Character letter: categories.toCharArray()) {
            boolean found = false;
            for(String search: questionCategories) {
                if(search.charAt(0)==letter) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
    }

    public static MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public static CreateQuestionConversationFactory getCreateQuestionFactory() {
        return createQuestionFactory;
    }

    public static ConfirmationFactory getConfirmationFactory() {
        return confirmationFactory;
    }

    public static List<AbstractGame> getGames() {
        return games;
    }

    public static File getQuestionDir() {
        return questionDir;
    }

    public static File getQuestionDataTable() {
        return questionDataTable;
    }

    public static File getSubmittedQuestionsFile() {
        return submittedQuestionsFile;
    }

    public static QuizGame getQuestionSubmitGame() {
        return questionSubmitGame;
    }

    public static File getQuestionCategoriesFile() {
        return questionCategoriesFile;
    }

    public static List<String> getQuestionCategories() {
        return questionCategories;
    }

    public static File getRaceDir() {
        return raceDir;
    }

    public static File getGolfDir() {
        return golfDir;
    }

    public static File getPvpDirectory() {
        return pvpDirectory;
    }

    public static File getLoadoutDirectory() {
        return loadoutDirectory;
    }

    public static void setPvpRunning(boolean pvpRunning) {
        PluginData.pvpRunning = pvpRunning;
    }
}
