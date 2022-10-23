package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.geoguessr.GeoGuessrConversation;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrAreas;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrBlacklist;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrSigns;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrWarps;
import com.mcmiddleearth.minigames.scoreboard.GeoGuessrGameScoreboard;
import com.mcmiddleearth.pluginutil.DynmapUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.TitleUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

import org.bukkit.event.player.PlayerMoveEvent;

import static com.mcmiddleearth.minigames.data.PluginData.getGame;

/**
 *
 * @author Jubo
 */

public class GeoGuessrGame extends AbstractGame implements Listener {

    private final int defaultGuessTime = 30;
    private final int defaultRadius = 10;

    private int radius = defaultRadius;
    private int guessTime = defaultGuessTime;

    private boolean started = false;

    //private boolean signHide = true;

    private boolean first = false;

    //private boolean points_bool = true;

    private final List<String> guidebook = new ArrayList<>();
    public final List<Player> hiddenPlayer = new ArrayList<>();
    private final List<UUID> leaveMessaged = new ArrayList<>();

    private Location warp;

    private String[][] warp_list;

    private final int defaultRoundNumber = 5;

    private int roundNumber = defaultRoundNumber;

    private int row = roundNumber - 1;

    private int points = 10;
    private int first_points = 12;

    private final String defaultArea = "a";
    private String area = defaultArea;

    private final Map<Player,Conversation> playersInRound = new HashMap<>();

    private final World world;

    private GeoGuessrSigns signs;

    private GeoGuessrAreas geoArea;

    private BossBar bar;

    public GeoGuessrGame(Player manager, String name) {
        super(manager, name, GameType.GEO_GUESSR, new GeoGuessrGameScoreboard());
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());
        setTeleportAllowed(false);
        setFlightAllowed(false);
        setGm2Forced(true);
        setCollision(false);
        setGlow(false);
        sendReminder(manager);
        world = manager.getWorld();
        geoArea = new GeoGuessrAreas(area);
        GeoGuessrSigns signs = new GeoGuessrSigns();
        this.signs = signs;

        BossBar bar = Bukkit.createBossBar(ChatColor.YELLOW+"GeoGuessr", BarColor.WHITE, BarStyle.SOLID);
        bar.setProgress(1.0);
        bar.setVisible(true);
        this.bar = bar;
    }

    public void setArea(String area){
        geoArea.setArea(area);
        this.area = area;
    }

    public void setRoundNumber(int roundNumber){
        ((GeoGuessrGameScoreboard)getBoard()).setRoundCount(roundNumber);
        this.roundNumber = roundNumber;
        this.row = roundNumber - 1;
    }

    public void restartGame(){
        this.row = roundNumber - 1;
        ((GeoGuessrGameScoreboard)getBoard()).restart();
        getXWarps();
        this.signs = new GeoGuessrSigns();
    }

    public String[][] getWarpList(World world) {

        GeoGuessrWarps geoGuessrWarps = new GeoGuessrWarps();
        String[][] warp_list = geoGuessrWarps.getWarps(world);
        geoGuessrWarps.disconnect();
        int warp_count = 0;
        if(!(geoArea.getName().equalsIgnoreCase("all"))) {
            double x = 0;
            double z = 0;
            for (int i = 0; i < warp_list.length; i++){
                x = Double.parseDouble(warp_list[i][1]);
                z = Double.parseDouble(warp_list[i][3]);
                if (x > geoArea.x1() && x < geoArea.x2() && z > geoArea.z1() && z < geoArea.z2()) {
                    warp_count++;
                }
            }
            if(warp_count < 5){
                setRoundNumber(warp_count);
            }
            String[][] area_warps = new String[warp_count][4];
            int j=0;
            for (int i = 0; i < warp_list.length; i++){
                x = Double.parseDouble(warp_list[i][1]);
                z = Double.parseDouble(warp_list[i][3]);
                if (x > geoArea.x1() && x < geoArea.x2() && z > geoArea.z1() && z < geoArea.z2()) {
                    area_warps[j][0] = warp_list[i][0];
                    area_warps[j][1] = warp_list[i][1];
                    area_warps[j][2] = warp_list[i][2];
                    area_warps[j][3] = warp_list[i][3];
                    j++;
                }
            }
            return area_warps;
        }
        return  warp_list;
    }

    public void getXWarps(){
        String[][] warp_list = getWarpList(world);
        String[][] x_warps = new String[roundNumber][4];
        int warp = 0;
        int i = 0;
        List<Integer> warp_rows = new ArrayList<>();
        warp_rows.add(-1);

        do{
            Random generator = new Random();
            warp = generator.nextInt(warp_list.length);
            if(checkBlacklist(warp_list[warp][0],warp_list.length)) {
                if (!warp_rows.contains(warp)) {
                    warp_rows.add(warp);
                    x_warps[i][0] = warp_list[warp][0];
                    x_warps[i][1] = warp_list[warp][1];
                    x_warps[i][2] = warp_list[warp][2];
                    x_warps[i][3] = warp_list[warp][3];
                    i++;
                }
            }
        }while(i <= row);
        this.warp_list = x_warps;
    }

    @Override
    public void addPlayer(Player player){
        super.addPlayer(player);
        ((GeoGuessrGameScoreboard)getBoard()).addPlayer(player.getName());
        hidePlayer(player);
        bar.addPlayer(player);
    }

    @Override
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);
        if (player.isOnline()) {
            Player player_on = player.getPlayer();
            unhidePlayer(player_on);
            bar.removePlayer(player_on);
            if (guidebook.contains(player_on.getName())) {
                com.mcmiddleearth.guidebook.data.PluginData.include(player_on);
            }
        }
    }

    @Override
    public String getGameChatTag(Player player) {
        if(player.getUniqueId().equals(UUID.fromString("b8d1ce5c-2b38-428c-9bb8-c8ee6ad58c4b")))
            return ChatColor.DARK_RED + "<Game Master ";
        if(PlayerUtil.isSame(getManager(), player)) {
            return ChatColor.DARK_AQUA + "<Host ";
        }
        else {
            return super.getGameChatTag(player);
        }
    }

    public void sendRound() {
        if (row == -1) {
            Player manager = Bukkit.getPlayer(getManager().getUniqueId());
            PluginData.getMessageUtil().sendInfoMessage(manager, "There are no more rounds. Please announce the winners, if not already done.");
        } else {
            setPoints();
            this.started = true;
            this.first = false;
            if (this.radius <= 0) {
                this.radius = this.defaultRadius;
            }
            double x = Double.parseDouble(warp_list[row][1]);
            double y = Double.parseDouble(warp_list[row][2]);
            double z = Double.parseDouble(warp_list[row][3]);

            bar.setProgress(1.0);
            (((GeoGuessrGameScoreboard) getBoard())).startRound(guessTime, this.countOnlinePlayer(),bar);

            GeoGuessrConversation geoGuessrConversation = new GeoGuessrConversation(MiniGamesPlugin.getPluginInstance(), guessTime);
            ((GeoGuessrGameScoreboard) getBoard()).addRound();

            warp = new Location(world, x, y, z);
            if(getSigns()){
                signs = new GeoGuessrSigns();
                signs.removeSigns(warp,radius);
            }

            for (Player player : getOnlinePlayers()) {
                teleportPlayer(player, x, y, z);
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
                }
            }
            row--;
        }
    }

    private void teleportPlayer(Player player, double x, double y, double z){
        Location location = new Location(player.getPlayer().getWorld(), x, y, z);
        forceTeleport(player,location);
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
        if(started){
            super.playerMove(event);
        }
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
                getWinHighscore().setGeoWin(player.getUniqueId());
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

    @Override
    public boolean joinAllowed() {
        return super.joinAllowed() && !started;
    }

    public boolean isStarted(){return this.started;}

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
        bar.setProgress(1.0);
        if(getSigns()){
            signs.replaceSigns();
        }
        if (row < 0) {
            bar.setTitle(ChatColor.YELLOW+"GeoGuessr");
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
        ((GeoGuessrGameScoreboard)getBoard()).score(player.getName(),points);
    }

    public boolean incrementFirstScore(Player player){
        if(!first){
            first = true;
            ((GeoGuessrGameScoreboard)getBoard()).firstScore(player.getName(),first_points);
            sendFirst(player);
            return true;
        }
        return false;
    }

    @Override
    public void end(Player sender){
        super.end(sender);
        if(getSigns()){
            signs.replaceSigns();
        }
        for(Player player : getOnlinePlayers()){
            bar.removePlayer(player);
        }
    }

    /*
    public void setSigns(boolean bool){
        if(!started){
            this.signHide = bool;
        }
    }
     */


    public void setPoints(){
        if(!started){
            if(!getPoints()){
                this.points = 1;
                this.first_points = 1;
            }else{
                this.points = 10;
                this.first_points = 12;
            }
        }
    }

    private boolean checkBlacklist(String warp,Integer length){
        GeoGuessrBlacklist Blacklist = new GeoGuessrBlacklist();
        Map <String,Object> blacklist = Blacklist.show();
        if((length - blacklist.size()) < roundNumber){
            return true;
        }
        if(blacklist.containsValue(warp)){
            return false;
        }else{
            return true;
        }
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

    private void sendReminder(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player,"Don´t forget to set the number of rounds [/game setrounds x] . Default is 5.");
        PluginData.getMessageUtil().sendInfoMessage(player,"Don´t forget to set the area of warps [/game setarea x] . Default is a = all.");
        PluginData.getMessageUtil().sendInfoMessage(player,"Switch signtext on with /game allow signs.");
        PluginData.getMessageUtil().sendInfoMessage(player,"Switch equal points on with /game deny points.");
        PluginData.getMessageUtil().sendInfoMessage(player,"Do /game ready when you are done or have nothing done.");
    }

    private void sendFirst(Player player){
        PluginData.getMessageUtil().sendInfoMessage(player,"You were the first to guess it correctly!");
    }
}
