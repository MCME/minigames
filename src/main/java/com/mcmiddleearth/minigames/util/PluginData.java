package com.mcmiddleearth.minigames.util;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 *
 * @author Jubo
 */
public class PluginData {

    private final static MessageUtil messageUtil = new MessageUtil();

    private static final File questionDir = new File(MiniGamesPlugin.getInstance().getDataFolder()+File.separator+"QuizQuestions");
    private static final File questionDataTable = new File(questionDir,"questionTable.dat");
    private static final File questionCategoriesFile = new File(questionDir,"questionCategories.dat");
    private static final List<String> questionCategories = new ArrayList<>();

    private static final List<AbstractGame> games = new ArrayList<>();


    static{
        if(!MiniGamesPlugin.getInstance().getDataFolder().exists()){
            MiniGamesPlugin.getInstance().getDataFolder().mkdirs();
        }

        if(!questionDir.exists()) {
            questionDir.mkdirs();
        }
    }

    public static void load(){
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
            MiniGamesPlugin.getInstance().getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public static boolean gameRunning(){
        for(AbstractGame game:games){
            if(game.isAnnounced())
                return true;
        }
        return false;
    }

    public static boolean isAlreadyAnnounced(McmeCommandSender sender){
        ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
        for(AbstractGame game:games)
            if(game.getManager().equals(player) || game.getPlayers().contains(player))
                    return game.isAnnounced();
        return false;
    }

    public static boolean isManager(McmeCommandSender sender){
        ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
        for(AbstractGame game:games){
            if(game.getManager().equals(player))
                return true;
        }
        return false;
    }

    public static boolean isInGame(McmeCommandSender sender){
        ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
        for(AbstractGame game:games){
            if(game.getPlayers().contains(player))
                return true;
        }
        return false;
    }

    public static boolean isInGame(ProxiedPlayer player){
        for(AbstractGame game: games)
            if(game.getPlayers().contains(player))
                return true;
        return false;
    }

    public static boolean isCorrectGameType(McmeCommandSender sender, GameType type){
        ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
        for(AbstractGame game:games){
            if((game.getPlayers().contains(player) || game.getManager().equals(player)) && type == game.getType() )
                return true;
        }
        return false;
    }

    public static boolean isCorrectGameType(ProxiedPlayer player, GameType type){
        for(AbstractGame game:games){
            if((game.getPlayers().contains(player) || game.getManager().equals(player)) && type == game.getType() )
                return true;
        }
        return false;
    }

    public static AbstractGame getGame(McmeCommandSender sender){
        ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
        for(AbstractGame game:games){
            if(game.getManager().equals(player) || game.getPlayers().contains(player)){
                return game;
            }
        }
        return null;
    }

    public static AbstractGame getGame(ProxiedPlayer player){
        for(AbstractGame game: games){
            if(game.getManager().equals(player) || game.getPlayers().contains(player)){
                return game;
            }
        }
        return null;
    }

    public static AbstractGame getGame(String name){
        for(AbstractGame game:games){
            if(game.getName().equals(name))
                return game;
        }
        return null;
    }

    public static List<String> getGames(){
        List<String> gameNames = new ArrayList<>();
        for(AbstractGame game: games){
            gameNames.add(game.getName());
        }
        return gameNames;
    }

    public static List<String> getManagerPerms(){
        List<String> manager = new ArrayList<>();
        for(ProxiedPlayer player: ProxyServer.getInstance().getPlayers()){
            if(player.hasPermission(Permission.MANAGER.getPermissionNode())){
                manager.add(player.getName());
            }
        }
        return manager;
    }

    public static void addGame(AbstractGame game){
        games.add(game);
    }

    public static void removeGame(AbstractGame game){
        games.remove(game);
    }

    public static void clearGames(){
        games.clear();
    }

    public static File getQuestionDir(){return questionDir;}

    public static File getQuestionDataTable() {
        return questionDataTable;
    }

    public static List<String> getQuestionCategoriesFile() {
        return questionCategories;
    }
    public static MessageUtil getMessageUtil(){return messageUtil;}

    public static boolean hasPermission(McmeCommandSender sender, Permission perm){
        ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
        return player.hasPermission(perm.getPermissionNode());
    }
}
