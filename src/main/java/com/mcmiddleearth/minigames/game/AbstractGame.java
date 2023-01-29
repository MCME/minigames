package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.scoreboard.AbstractGameScoreboard;
import com.mcmiddleearth.minigames.util.ChatRanks;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Jubo
 */
public abstract class AbstractGame {

    private ProxiedPlayer manager;
    private final String name;
    private final GameType type;
    private final AbstractGameScoreboard board;
    private final List<ProxiedPlayer> players = new ArrayList<>();

    private static List<ProxiedPlayer> inConversation = new ArrayList<>();

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
        toggleConfig.put("privat",false);
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

    public AbstractGame(ProxiedPlayer manager, String name, GameType type, AbstractGameScoreboard board){
        this.manager = manager;
        this.name = name;
        this.type = type;
        this.board = board;
    }

    public void addPlayer(ProxiedPlayer player){
        players.add(player);
        getBoard().addPlayer(player);
        notifyGame("Everybody welcome "+player.getName()+" to the game!");
        PluginData.getMessageUtil().sendInfoMessage(player,"Welcome to the game.");
    }

    public void selfDestruction(){
        notifyGame("The host was disconnected from the server. This tour will destroy itself in 60 seconds.");
        task = true;
        cleanup = ProxyServer.getInstance().getScheduler().schedule(MiniGamesPlugin.getInstance(),() -> {
            if(!manager.isConnected())
                endGame();
        }, 60, TimeUnit.SECONDS);
    }

    public void returnedManager(ProxiedPlayer player){
        this.manager = player;
        players.add(player);
        cleanup.cancel();
        task = false;
        notifyGame("The manager has returned. Destruction prevented.");
    }

    public void endGame(){
        notifyGame("The game has ended.");
        PluginData.getMessageUtil().sendInfoMessage(manager,"You ended the game.");
        for(ProxiedPlayer player: players)
            getBoard().removePlayer(player);
        players.clear();
        PluginData.removeGame(this);
    }

    public void removePlayer(ProxiedPlayer player){
        if(player == manager){
            endGame();
            return;
        }
        getBoard().removePlayer(player);
        players.remove(player);
        notifyGame(player.getName()+" has left the game.");
        PluginData.getMessageUtil().sendInfoMessage(player,"You left the game.");
    }

    public void kickPlayer(ProxiedPlayer player){
        if(player != manager){
            removePlayer(player);
            PluginData.getMessageUtil().sendErrorMessage(player,"You were kicked from the game. Think about it!");
            PluginData.getMessageUtil().sendInfoMessage(manager,"You kicked " + player.getName() + " from the game.");
        }else{
            PluginData.getMessageUtil().sendErrorMessage(player,"You can´t kick yourself idiot.");
        }
    }

    public static void setDeleteConversation(boolean bool){deleteConversation = bool;}

    public static void setDeleteFile(File file){deleteFile = file;}
    public static File getDeleteFile(){return deleteFile;}

    public static boolean deleteConversation(){return deleteConversation; }

    public static void addConversation(ProxiedPlayer player){inConversation.add(player);}

    public static List<ProxiedPlayer> getInConversation(){return inConversation;}

    public static void removeConversation(ProxiedPlayer player){inConversation.remove(player);}


    public void setSaveConversation(boolean saveConversation){this.saveConversation = saveConversation;}

    public boolean saveConversation(){return saveConversation;}

    public void setSaveInfos(File file,String description){
        saveFile = file;
        saveDescription = description;
        setSaveConversation(true);
    }

    public String getSaveDescription(){return saveDescription;}
    public File getSaveFile(){return saveFile;}

    public void setManager(ProxiedPlayer manager){
        this.manager = manager;
    }

    public int countPlayer(){return players.size();}

    protected void notifyGame(String message){
        for(ProxiedPlayer player: players){
            PluginData.getMessageUtil().sendInfoMessage(player,message);
        }
    }

    public void gameChat(ProxiedPlayer player, String message){
        String chatMessage;
        if(player.getName().equals("Jubo"))
            chatMessage = ChatRanks.GAME_MASTER.getChatPrefix() + player.getName() + ChatColor.WHITE + ": " + message;
        else if(player.equals(manager))
            chatMessage = ChatRanks.MANAGER.getChatPrefix() + player.getName() + ChatColor.WHITE + ": " + message;
        else
            chatMessage = ChatRanks.Participant.getChatPrefix() + player.getName() + ChatColor.WHITE + ": " + message;
        for(ProxiedPlayer receiver: players){
            receiver.sendMessage(new ComponentBuilder(chatMessage).create());
        }
    }

    public String getGameChatTag(ProxiedPlayer player) {
        if(player == manager) {
            return ChatColor.DARK_AQUA + "<Manager ";
        }
        else {
            return ChatColor.BLUE + "<Participant ";
        }
    }

    public void announceGame(){
        announced = true;
        String message = Style.INFO+PluginData.getMessageUtil().getPREFIX()+Style.STRESSED+manager.getName()+Style.INFO+
                " started a new game "+Style.STRESSED+type.toString()+Style.INFO+" game. To play that game, type in chat: "+
                Style.STRESSED+"/newgame join "+name+Style.INFO+" or "+Style.HIGHLIGHT+"Click here";
        for(ProxiedPlayer player: ProxyServer.getInstance().getPlayers())
            PluginData.getMessageUtil().sendClickableInfoMessage(player,message,"/newgame join "+name);
    }

    public static Map<String,Boolean> getConfig(){return toggleConfig;}
    public AbstractGameScoreboard getBoard(){return board;}
    public String getName(){return name;}
    public GameType getType(){return type;}
    public List<ProxiedPlayer> getPlayers(){return players;}
    public ProxiedPlayer getManager(){return manager;}
    public boolean isAnnounced(){return announced;}

}