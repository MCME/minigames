/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.data;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.confirmation.ConfirmationFactory;
import com.mcmiddleearth.minigames.conversation.quiz.CreateQuestionConversationFactory;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private static final File highscoreDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder() + File.separator + "Highscore");
    private static final File geoGuessrDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder() + File.separator + "GeoGuessr");
    private static final File werewolfDir = new File (MiniGamesPlugin.getPluginInstance().getDataFolder() + File.separator + "Werewolf");

    private static final File geoGuessrBlacklist = new File(geoGuessrDir,"blacklist.yml");
    private static final File geoGuessrRestoreDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder() + File.separator + "GeoGuessrRestore");
    private static final File golfDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Courses");
    private static final File pvpDirectory = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Arenas");
    private static final File loadoutDirectory = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Loadouts");

    public static boolean pvpRunning = false;

    public static Integer startTimer = 0;
    private static BukkitRunnable timerTask;

    private static FileConfiguration werewolfBooksConfig;
    
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

        if(!geoGuessrDir.exists()){
            geoGuessrDir.mkdirs();
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

        if(!werewolfDir.exists()){
            werewolfDir.mkdirs();
            File werewolfConfig = new File(werewolfDir,"werewolfConfig.yml");
            try {
                werewolfConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!geoGuessrRestoreDir.exists()){
            geoGuessrRestoreDir.mkdirs();
            File restore = new File(geoGuessrRestoreDir,"restoreGeo.yml");
            try {
                restore.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!geoGuessrBlacklist.exists()){
            try {
                geoGuessrBlacklist.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(!highscoreDir.exists()){
            highscoreDir.mkdirs();
            File highscore = new File(highscoreDir,"highscore.yml");
            try {
                highscore.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(highscore);
                config.createSection("hide");
                config.getConfigurationSection("hide").createSection("seek");
                config.getConfigurationSection("hide").createSection("hide");
                config.save(highscore);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File racetime = new File(highscoreDir,"racetime.yml");
            try {
                racetime.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        werewolfBooksConfig = YamlConfiguration.loadConfiguration(new File(PluginData.getWerewolfDir(),"werewolfConfig.yml"));
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

    public static Integer getTimer(){
        return startTimer;
    }

    public static void setTime(Integer time){
        startTimer = time;
    }

    public static void startTimerRunnable(Player manager){
        if(startTimer == 0) return;

        Plugin connectPlugin = Bukkit.getPluginManager().getPlugin("MCME-Connect");
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if(timerTask != null) timerTask.cancel();
        timerTask = new BukkitRunnable(){
            @Override
            public void run(){
                if(startTimer==0) timerTask.cancel();
                if(startTimer == 60 || startTimer == 30 || startTimer == 10) {
                    if (player != null && connectPlugin != null && connectPlugin.isEnabled()) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Message");
                        out.writeUTF("ALL");
                        String message = getMessageUtil().INFO + "The game " + getGame(manager).getName() + " will start in " + startTimer + " seconds.";
                        out.writeUTF(message);
                        player.sendPluginMessage(MiniGamesPlugin.getPluginInstance(), "BungeeCord", out.toByteArray());
                        Logger.getGlobal().info("Bungee Broadcast sent! " + message);
                    } else {
                        getMessageUtil().sendBroadcastMessage("The game " + getGame(manager).getName() + " will start in " + startTimer + " seconds.");
                    }
                }
                startTimer = startTimer - 1;
            }
        };
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(),1,20);
    }

    public static FileConfiguration getWerewolfBooks() {return werewolfBooksConfig;}

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

    public static File getWerewolfDir(){
        return werewolfDir;
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

    public static File getGeoGuessrDir(){return geoGuessrDir;}

    public static File getGeoGuessrRestoreDir(){return geoGuessrRestoreDir;}

    public static File getHighscoreDir(){
        return highscoreDir;
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
