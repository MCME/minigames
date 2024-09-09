package com.mcmiddleearth.minigames.core.game;

import com.mcmiddleearth.base.core.message.Message;
import com.mcmiddleearth.base.core.message.MessageColor;
import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.base.core.taskScheduling.Task;
import com.mcmiddleearth.minigames.bungee.MiniGamesBungeePlugin;
import com.mcmiddleearth.minigames.core.MiniGames;
import com.mcmiddleearth.minigames.core.scoreboard.AbstractGameScoreboard;
import com.mcmiddleearth.minigames.core.util.ChatRanks;
import com.mcmiddleearth.minigames.core.util.PluginData;
import com.mcmiddleearth.minigames.core.util.Style;

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

    private McmeProxyPlayer manager;
    private final String name;
    private final GameType type;
    private final AbstractGameScoreboard board;
    private final List<McmeProxyPlayer> players = new ArrayList<>();

    private static final List<McmeProxyPlayer> inConversation = new ArrayList<>();

    private boolean saveConversation = false;
    private File saveFile;
    private String saveDescription;

    private static File deleteFile;
    private static boolean deleteConversation = false;

    private boolean announced = false;

    private Task cleanup;
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

    public AbstractGame(McmeProxyPlayer manager, String name, GameType type, AbstractGameScoreboard board){
        this.manager = manager;
        this.name = name;
        this.type = type;
        this.board = board;
    }

    public void addPlayer(McmeProxyPlayer player){
        players.add(player);
        getBoard().addPlayer(player);
        notifyGame("Everybody welcome "+player.getName()+" to the game!");
        PluginData.getMessageUtil().sendInfoMessage(player,"Welcome to the game.");
    }

    public void selfDestruction(){
        notifyGame("The host was disconnected from the server. This tour will destroy itself in 60 seconds.");
        task = true;
        cleanup = MiniGames.getPlugin().getTask(() -> {
            if(!manager.isConnected())
                endGame();
        }).schedule(60, TimeUnit.SECONDS);
    }

    public void returnedManager(McmeProxyPlayer player){
        this.manager = player;
        players.add(player);
        cleanup.cancel();
        task = false;
        notifyGame("The manager has returned. Destruction prevented.");
    }

    public void endGame(){
        notifyGame("The game has ended.");
        PluginData.getMessageUtil().sendInfoMessage(manager,"You ended the game.");
        for(McmeProxyPlayer player: players)
            getBoard().removePlayer(player);
        players.clear();
        PluginData.removeGame(this);
    }

    public void removePlayer(McmeProxyPlayer player){
        if(player == manager){
            endGame();
            return;
        }
        getBoard().removePlayer(player);
        players.remove(player);
        notifyGame(player.getName()+" has left the game.");
        PluginData.getMessageUtil().sendInfoMessage(player,"You left the game.");
    }

    public void kickPlayer(McmeProxyPlayer player){
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

    public static void addConversation(McmeProxyPlayer player){inConversation.add(player);}

    public static List<McmeProxyPlayer> getInConversation(){return inConversation;}

    public static void removeConversation(McmeProxyPlayer player){inConversation.remove(player);}


    public void setSaveConversation(boolean saveConversation){this.saveConversation = saveConversation;}

    public boolean saveConversation(){return saveConversation;}

    public void setSaveInfos(File file,String description){
        saveFile = file;
        saveDescription = description;
        setSaveConversation(true);
    }

    public String getSaveDescription(){return saveDescription;}
    public File getSaveFile(){return saveFile;}

    public void setManager(McmeProxyPlayer manager){
        this.manager = manager;
    }

    public int countPlayer(){return players.size();}

    protected void notifyGame(String message){
        for(McmeProxyPlayer player: players){
            PluginData.getMessageUtil().sendInfoMessage(player,message);
        }
    }

    public void gameChat(McmeProxyPlayer player, String message){
        String chatMessage;
        if(player.getName().equals("Jubo"))
            chatMessage = ChatRanks.GAME_MASTER.getChatPrefix() + player.getName() + ChatColor.WHITE + ": " + message;
        else if(player.equals(manager))
            chatMessage = ChatRanks.MANAGER.getChatPrefix() + player.getName() + ChatColor.WHITE + ": " + message;
        else
            chatMessage = ChatRanks.Participant.getChatPrefix() + player.getName() + ChatColor.WHITE + ": " + message;
        for(McmeProxyPlayer receiver: players){
            receiver.sendMessage(new ComponentBuilder(chatMessage).create());
        }
    }

    public Message getGameChatTag(McmeProxyPlayer player) {
        if(player == manager) {
            return MiniGames.message("<Manager ", MessageColor.DARK_AQUA);
        }
        else {
            return MiniGames.message("<Participant ", MessageColor.BLUE);
        }
    }

    public void announceGame(){
        announced = true;
        String message = Style.INFO+PluginData.getMessageUtil().getPREFIX()+Style.STRESSED+manager.getName()+Style.INFO+
                " started a new game "+Style.STRESSED+type.toString()+Style.INFO+" game. To play that game, type in chat: "+
                Style.STRESSED+"/game join "+name+Style.INFO+" or "+Style.HIGHLIGHT+"Click here";
        for(McmeProxyPlayer player: MiniGames.getProxy().getPlayers())
            PluginData.getMessageUtil().sendClickableInfoMessage(player,message,"/game join "+name);
    }

    public static Map<String,Boolean> getConfig(){return toggleConfig;}
    public AbstractGameScoreboard getBoard(){return board;}
    public String getName(){return name;}
    public GameType getType(){return type;}
    public List<McmeProxyPlayer> getPlayers(){return players;}
    public McmeProxyPlayer getManager(){return manager;}
    public boolean isAnnounced(){return announced;}

}