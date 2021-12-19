package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.geoguessr.GeoGuessrConversation;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrWarps;
import com.mcmiddleearth.minigames.scoreboard.GeoGuessrGameScoreboard;
import com.mcmiddleearth.pluginutil.DynmapUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.TitleUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.*;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

import org.bukkit.event.player.PlayerMoveEvent;


import static com.mcmiddleearth.minigames.data.PluginData.getGame;
//import static com.mcmiddleearth.minigames.geoGuessr.GeoGuessrWarps.getWarps_test;

/**
 *
 * @author Jubo
 */

public class GeoGuessrGame extends AbstractGame implements Listener {

    private final int defaultGuessTime = 30;
    private final int defaultRadius = 10;

    private int radius = defaultRadius;
    private int guessTime = defaultGuessTime;

    private final List<String> guidebook = new ArrayList<>();

    public final List<Player> hiddenPlayer = new ArrayList<>();

    private Location warp;

    private String[][] warp_list;

    private int row = 4;

    private final Map<Player,Conversation> playersInRound = new HashMap<>();

    public GeoGuessrGame(Player manager, String name) {
        super(manager, name, GameType.GEO_GUESSR, new GeoGuessrGameScoreboard());

        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());
        setTeleportAllowed(true);
        setFlightAllowed(false);
        setGm2Forced(true);
        setCollision(true);
        UUID uuid = manager.getWorld().getUID();
        warp_list = get5Warps(uuid);
        announceGame();
    }

    public String[][] getWarpList(UUID uuid) {
        //String[][] warp_list = getWarps_test();
        //return warp_list;
        GeoGuessrWarps geoGuessrWarps = new GeoGuessrWarps();
        warp_list = geoGuessrWarps.getWarps(uuid);
        geoGuessrWarps.disconnect();
        return  warp_list;
    }

    public String[][] get5Warps(UUID uuid){
        String[][] warp_list = getWarpList(uuid);
        String[][] five_warps = new String[5][4];
        int warp = 0;
        int i = 0;
        List<Integer> warp_rows = new ArrayList<>();
        warp_rows.add(-1);

        do{
            Random generator = new Random();
            warp = generator.nextInt(warp_list.length);
            if(!warp_rows.contains(warp)){
                warp_rows.add(warp);
                five_warps[i][0] = warp_list[warp][0];
                five_warps[i][1] = warp_list[warp][1];
                five_warps[i][2] = warp_list[warp][2];
                five_warps[i][3] = warp_list[warp][3];
                i++;
            }
        }while(i <= row);
        return five_warps;
    }

    @Override
    public void addPlayer(Player player){
        super.addPlayer(player);
        ((GeoGuessrGameScoreboard)getBoard()).addPlayer(player.getName());
        hidePlayer(player);
    }

    @Override
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);
        if (player.isOnline()) {
            Player player_on = player.getPlayer();
            unhidePlayer(player_on);
            if (guidebook.contains(player_on.getName())) {
                com.mcmiddleearth.guidebook.data.PluginData.include(player_on);
            }
        }
    }

    @Override
    public void end(Player player){
        super.end(player);
        unhidePlayer(player);
        if (guidebook.contains(player.getName())) {
            com.mcmiddleearth.guidebook.data.PluginData.include(player);
        }
    }

    @Override
    public String getGameChatTag(Player player) {
        if(PlayerUtil.isSame(getManager(), player)) {
            return ChatColor.DARK_AQUA + "<Host ";
        }
        else {
            return super.getGameChatTag(player);
        }
    }
    // Make chat clickable to give book of warps
    public void sendRound(){
            if (row == -1) {
                for (Player player : getOnlinePlayers()) {
                    player.sendMessage("The game is over. If not already done, announce the winner!");
                }
            } else {
                if (this.radius <= 0) {
                    this.radius = this.defaultRadius;
                }
                double x = Double.parseDouble(warp_list[row][1]);
                double y = Double.parseDouble(warp_list[row][2]);
                double z = Double.parseDouble(warp_list[row][3]);

                (((GeoGuessrGameScoreboard) getBoard())).startRound(guessTime, this.countOnlinePlayer());

                GeoGuessrConversation geoGuessrConversation = new GeoGuessrConversation(MiniGamesPlugin.getPluginInstance(), guessTime);
                ((GeoGuessrGameScoreboard) getBoard()).addRound();

                for (Player player : getOnlinePlayers()) {
                    warp = new Location(player.getWorld(), x, y, z);
                    Location location = new Location(player.getPlayer().getWorld(), x, y, z);
                    player.teleport(location);
                    if (player.isConversing()) {
                        PluginData.getMessageUtil().sendErrorMessage(player, "Can't send the next Location to you as you are already in another conversation.");
                    } else {
                        giveWarpbook(player);
                        if (!com.mcmiddleearth.guidebook.data.PluginData.isExcluded(player)) {
                            guidebook.add(player.getName());
                        }
                        com.mcmiddleearth.guidebook.data.PluginData.exclude(player);
                        Conversation newConvo = geoGuessrConversation.start(player, this, warp_list[row][0]);
                        playersInRound.put(player, newConvo);
                        PluginData.getMessageUtil().sendInfoMessage(player, "Type the correct warp in.");
                        //Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());
                        //Bukkit.getWorld("world").setGameRule(GameRule.REDUCED_DEBUG_INFO,true);
                    }
                }
                row--;
            }
        }

    public void hidePlayer(Player player){
        hiddenPlayer.add(player);
        DynmapUtil.hide(player);
    }

    public void unhidePlayer(Player player){
        hiddenPlayer.remove(player);
        DynmapUtil.show(player);
    }

    @Override
    public Location getWarp(){
        return warp;
    }

    public void playerMove(PlayerMoveEvent event) {
        super.playerMove(event);
    }

    @Override
    public int allowedRadius(Player player) {
        return Math.abs(radius);
    }



    public boolean announceWinner(boolean allowEqual){
        int maxScore = 0;
        List<Player> winner = new ArrayList<>();

        for(Player player: getOnlinePlayers()){
            int score =  ((GeoGuessrGameScoreboard)getBoard()).getScore(player.getName());
            if(score > maxScore) {
                maxScore = score;
                winner.clear();
                winner.add(player);
            }else if(score == maxScore){
                winner.add(player);
            }
        }
        if(winner.size()>0 && (allowEqual || winner.size()==1)){
            for(Player player: winner){
                TitleUtil.showTitle(player,ChatColor.GOLD+"Congrats","You won the GeoGuessr game.");
                String winnerNames = winner.get(0).getName();
                for(int i = 1; i<winner.size()-1;i++){
                    winnerNames = winnerNames + ", " + winner.get(i).getName();
                }
                if(winner.size()>1) {
                    winnerNames = winnerNames + " and "+winner.get(winner.size()-1).getName();
                }
                TitleUtil.showTitleAll(getOnlinePlayers(),winner,ChatColor.BLUE+"Game Over",winnerNames+" won the GeoGuessr game.");
                Player manager = Bukkit.getPlayer(getManager().getUniqueId());
                if(manager!= null&& !PluginData.isInGame(manager)){
                    TitleUtil.showTitle(manager, ChatColor.BLUE+"Game Over", winnerNames+" won the GeoGuessr game.");
                }
            }
            return true;
        }
        return false;
    }

    public void setGuessTime(int guessTime) {
        this.guessTime = guessTime;
    }

    public void setGuessRadius(int radius){
        this.radius = radius;
    }

    private void sendManagerWinnerInfo(Player manager) {
        PluginData.getMessageUtil().sendInfoMessage(manager, "There is no single winner. You can announce multiple winners with /game winner");
    }

    private void giveWarpbook(Player player) {
        FancyMessage message = new FancyMessage(MessageType.HIGHLIGHT, PluginData.getMessageUtil())
                        .addClickable("Click here to get the Warpbook", "/library give Warps");
        message.send(player);
    }

    public void removePlayerFromRound(Player player){
        Player found = null;
        for(Player search: playersInRound.keySet()){
            if(search.getUniqueId()==player.getUniqueId()){
                found = search;
                break;
            }
        }
        if(found!=null){
            playersInRound.remove(found);
            ((GeoGuessrGameScoreboard)getBoard()).playerFinished();
        }
    }

    public boolean isPlayerInRound(){
        return !playersInRound.isEmpty();
    }

    public void stopRound() {
        ((GeoGuessrGameScoreboard) getBoard()).stopRound();
        removeAllPlayersFromRound();
        if (row < 0) {
            if (!announceWinner(false)) {
                Player manager = Bukkit.getPlayer(getManager().getUniqueId());
                if (manager != null) {
                    sendManagerWinnerInfo(manager);
                }
            }
        }
    }

    public void removeAllPlayersFromRound(){
        List<Conversation> foundConvos = new ArrayList<>();
        for(Player player: playersInRound.keySet()){
            Conversation convo = playersInRound.get(player);
            if(convo != null && convo.getState().equals(Conversation.ConversationState.STARTED)){
                foundConvos.add(convo);
            }
        }
        for(Conversation convo:foundConvos){
            convo.abandon();
        }
        playersInRound.clear();
    }

    public void incrementScore(Player player){
        ((GeoGuessrGameScoreboard)getBoard()).score(player.getName());
    }

    public void GeoGameWinner(Player player){
        AbstractGame game = getGame(player);
        GeoGuessrGame geogame = (GeoGuessrGame) game;
        if(!geogame.announceWinner(true)){
            sendNoWinnerMessage(player);
        }
    }

    private void sendNoWinnerMessage(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "There is no winner.");
    }

}
