package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.scoreboard.generics.AbstractGameScoreboard;
import com.mcmiddleearth.minigames.util.ChatRanks;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Jubo
 */
public abstract class AbstractGame {

    private Player manager;
    private final String name;
    private final GameType type;
    private final AbstractGameScoreboard board;
    private final List<Player> players = new ArrayList<>();

    private static final List<Player> inConversation = new ArrayList<>();

    private boolean saveConversation = false;
    private File saveFile;
    private String saveDescription;

    private static File deleteFile;
    private static boolean deleteConversation = false;

    private boolean announced = false;

    private ScheduledTask cleanup;
    private boolean task = false;

    private static final Map<String,Boolean> toggleConfig = new HashMap<>();

    static {
        toggleConfig.put("flight",false);
        toggleConfig.put("teleport",false);
        toggleConfig.put("private",false);
        toggleConfig.put("warp",false);
        toggleConfig.put("spectate",true);
        toggleConfig.put("save",true);
        toggleConfig.put("collision",true);
        toggleConfig.put("invisible",true);
        toggleConfig.put("signs",true);
        toggleConfig.put("glow",false);
        toggleConfig.put("throwable",true);
        toggleConfig.put("points",true);
    }

    public AbstractGame(Player manager, String name, GameType type, AbstractGameScoreboard board){
        this.manager = manager;
        this.name = name;
        this.type = type;
        this.board = board;
    }

    public void addPlayer(Player player){
        players.add(player);
        getBoard().addPlayer(player);
        notifyGame("Everybody welcome "+player.getUsername()+" to the game!");
        PluginData.getMessageUtil().sendInfoMessage(player,"Welcome to the game.");
    }

    public void selfDestruction(){
        notifyGame("The host was disconnected from the server. This tour will destroy itself in 60 seconds.");
        task = true;
        cleanup = MiniGamesPlugin.getInstance().getProxyServer().getScheduler().buildTask(MiniGamesPlugin.getInstance(),() -> {
            if(!manager.isActive())
                endGame();
        }).delay(60, TimeUnit.SECONDS).schedule();
    }

    public void returnedManager(Player player){
        this.manager = player;
        players.add(player);
        cleanup.cancel();
        task = false;
        notifyGame("The manager has returned. Destruction prevented.");
    }

    public void endGame(){
        notifyGame("The game has ended.");
        PluginData.getMessageUtil().sendInfoMessage(manager,"You ended the game.");
        for(Player player: players)
            getBoard().removePlayer(player);
        players.clear();
        PluginData.removeGame(this);
    }

    public void removePlayer(Player player){
        if(player == manager){
            endGame();
            return;
        }
        getBoard().removePlayer(player);
        players.remove(player);
        notifyGame(player.getUsername()+" has left the game.");
        PluginData.getMessageUtil().sendInfoMessage(player,"You left the game.");
    }

    public void kickPlayer(Player player){
        if(player != null && player != manager){
            removePlayer(player);
            PluginData.getMessageUtil().sendErrorMessage(player,"You were kicked from the game. Think about it!");
            PluginData.getMessageUtil().sendInfoMessage(manager,"You kicked " + player.getUsername() + " from the game.");
        }else{
            PluginData.getMessageUtil().sendErrorMessage(player,"You can´t kick yourself idiot.");
        }
    }

    public static void setDeleteConversation(boolean bool){deleteConversation = bool;}

    public static void setDeleteFile(File file){deleteFile = file;}
    public static File getDeleteFile(){return deleteFile;}

    public static boolean deleteConversation(){return deleteConversation; }

    public static void addConversation(Player player){inConversation.add(player);}

    public static List<Player> getInConversation(){return inConversation;}

    public static void removeConversation(Player player){inConversation.remove(player);}


    public void setSaveConversation(boolean saveConversation){this.saveConversation = saveConversation;}

    public boolean saveConversation(){return saveConversation;}

    public void setSaveInfos(File file,String description){
        saveFile = file;
        saveDescription = description;
        setSaveConversation(true);
    }

    public String getSaveDescription(){return saveDescription;}
    public File getSaveFile(){return saveFile;}

    public void setManager(Player manager){
        this.manager = manager;
    }

    public int countPlayer(){return players.size();}

    protected void notifyGame(String message){
        for(Player player: players){
            PluginData.getMessageUtil().sendInfoMessage(player,message);
        }
    }

    public void gameChat(Player player, String message){
        String chatMessage;
        if(player.getUsername().equals("Jubo"))
            chatMessage = ChatRanks.GAME_MASTER.getChatPrefix() + player.getUsername() + NamedTextColor.WHITE + ": " + message;
        else if(player.equals(manager))
            chatMessage = ChatRanks.MANAGER.getChatPrefix() + player.getUsername() + NamedTextColor.WHITE + ": " + message;
        else
            chatMessage = ChatRanks.Participant.getChatPrefix() + player.getUsername() + NamedTextColor.WHITE + ": " + message;
        for(Player receiver: players){
            receiver.sendMessage(Component.text(chatMessage));
        }
    }

    public String getGameChatTag(Player player) {
        if(player == manager) {
            return NamedTextColor.DARK_AQUA + "<Manager ";
        }
        else {
            return NamedTextColor.BLUE + "<Participant ";
        }
    }

    public void announceGame(){
        announced = true;
        String message = Style.INFO+PluginData.getMessageUtil().getPREFIX()+Style.STRESSED+manager.getUsername()+Style.INFO+
                " started a new game "+Style.STRESSED+type.toString()+Style.INFO+" game. To play that game, type in chat: "+
                Style.STRESSED+"/game join "+name+Style.INFO+" or "+Style.HIGHLIGHT+"Click here";
        for(Player player: MiniGamesPlugin.getInstance().getProxyServer().getAllPlayers())
            PluginData.getMessageUtil().sendClickableInfoMessage(player,message,"/game join "+name);
    }

    public static Map<String,Boolean> getConfig(){return toggleConfig;}
    public AbstractGameScoreboard getBoard(){return board;}
    public String getName(){return name;}
    public GameType getType(){return type;}
    public List<Player> getPlayers(){return players;}
    public Player getManager(){return manager;}
    public boolean isAnnounced(){return announced;}

}