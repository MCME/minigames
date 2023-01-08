package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jubo
 */
public abstract class AbstractGame {

    private ProxiedPlayer manager;
    private final String name;
    private final GameType type;
    private final GameScoreboard board;
    private final List<ProxiedPlayer> players = new ArrayList<>();

    private boolean announced = false;

    public AbstractGame(ProxiedPlayer manager, String name, GameType type, GameScoreboard board){
        this.manager = manager;
        this.name = name;
        this.type = type;
        this.board = board;
    }

    public void addPlayer(ProxiedPlayer player){
        players.add(player);
        notifyGame("Everybody welcome"+player.getName()+"to the game!");
        PluginData.getMessageUtil().sendInfoMessage(player,"Welcome to the game.");
    }

    public void endGame(){
        notifyGame("The game has ended.");
        players.clear();
        PluginData.removeGame(this);
    }

    public void removePlayer(ProxiedPlayer player){
        if(player == manager){
            endGame();
            return;
        }
        players.remove(player);
        notifyGame(player.getName()+" has left the game.");
        PluginData.getMessageUtil().sendInfoMessage(player,"You left the game.");
    }

    public void setManager(ProxiedPlayer manager){
        this.manager = manager;
    }

    public int countPlayer(){return players.size();}

    protected void notifyGame(String message){
        for(ProxiedPlayer player: players){
            PluginData.getMessageUtil().sendInfoMessage(player,message);
        }
    }

    public void tourChat(ProxiedPlayer player, String message){

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
                "started a new game "+Style.STRESSED+type.toString()+Style.INFO+" game. To play that game, type in chat: "+
                Style.STRESSED+"/game join "+name+Style.INFO+" or "+Style.HIGHLIGHT+"Click here";
        for(ProxiedPlayer player: ProxyServer.getInstance().getPlayers())
            PluginData.getMessageUtil().sendClickableInfoMessage(player,message,"/game join "+name);
    }

    public GameScoreboard getBoard(){return board;}
    public String getName(){return name;}
    public GameType getType(){return type;}
    public List<ProxiedPlayer> getPlayers(){return players;}
    public ProxiedPlayer getManager(){return manager;}
    public boolean isAnnounced(){return announced;}

}
