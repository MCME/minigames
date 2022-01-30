package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.geoguessr.GeoGuessrConversation;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrAreas;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


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

    private final List<String> guidebook = new ArrayList<>();
    public final List<Player> hiddenPlayer = new ArrayList<>();
    private final List<UUID> leaveMessaged = new ArrayList<>();

    private Location warp;

    private String[][] warp_list;

    private final int defaultRoundNumber = 5;

    private int roundNumber = defaultRoundNumber;

    private int row = roundNumber - 1;

    private final GeoGuessrAreas defaultArea = GeoGuessrAreas.All;
    private GeoGuessrAreas area = defaultArea;

    private final Map<Player,Conversation> playersInRound = new HashMap<>();

    private final UUID uuid;

    public GeoGuessrGame(Player manager, String name) {
        super(manager, name, GameType.GEO_GUESSR, new GeoGuessrGameScoreboard());

        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());
        setTeleportAllowed(true);
        setFlightAllowed(false);
        setGm2Forced(true);
        setCollision(true);
        sendReminder(manager);
        uuid = manager.getWorld().getUID();
    }

    public void setArea(GeoGuessrAreas area){
        this.area = area;
    }

    public void setRoundNumber(int roundNumber){
        ((GeoGuessrGameScoreboard)getBoard()).setRoundCount(roundNumber);
        this.roundNumber = roundNumber;
        this.row = roundNumber - 1;
    }

    public String[][] getWarpList(UUID uuid) {

        GeoGuessrWarps geoGuessrWarps = new GeoGuessrWarps();
        String[][] warp_list = geoGuessrWarps.getWarps(uuid);
        geoGuessrWarps.disconnect();
        //String[][] warp_list = geoGuessrWarps.getWarps_test();
        int warp_count = 0;
        if(area != GeoGuessrAreas.All) {
            double x = 0;
            double z = 0;
            for (int i = 0; i < warp_list.length; i++){
                x = Double.parseDouble(warp_list[i][1]);
                z = Double.parseDouble(warp_list[i][3]);
                if (x > area.x1() && x < area.x2() && z > area.z1() && z < area.z2()) {
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
                if (x > area.x1() && x < area.x2() && z > area.z1() && z < area.z2()) {
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
        String[][] warp_list = getWarpList(uuid);
        String[][] x_warps = new String[roundNumber][4];
        int warp = 0;
        int i = 0;
        List<Integer> warp_rows = new ArrayList<>();
        warp_rows.add(-1);

        do{
            Random generator = new Random();
            warp = generator.nextInt(warp_list.length);
            if(!warp_rows.contains(warp)){
                warp_rows.add(warp);
                x_warps[i][0] = warp_list[warp][0];
                x_warps[i][1] = warp_list[warp][1];
                x_warps[i][2] = warp_list[warp][2];
                x_warps[i][3] = warp_list[warp][3];
                i++;
            }
        }while(i <= row);
        this.warp_list = x_warps;
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

    public void sendRound() {
        if (row == -1) {
            Player manager = Bukkit.getPlayer(getManager().getUniqueId());
            PluginData.getMessageUtil().sendInfoMessage(manager, "There are no more rounds. Please announce the winners, if not already done.");
        } else {
            this.started = true;
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
                    /*
                    Plugin plugin = MiniGamesPlugin.getPluginInstance();

                    for(Player player2 : getOnlinePlayers()) {
                        player.hidePlayer(plugin, player2);
                    }
                     */
                warp = new Location(player.getWorld(), x, y, z);
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
        /*
        Random generator = new Random();
        int x_temp = generator.nextInt(this.radius-1);
        int z_temp = generator.nextInt(this.radius-1);

        int i = generator.nextInt(2);
        int j = generator.nextInt(2);

        if(i == 0 && j == 0){
            x = x - x_temp;
            z = z - z_temp;
        }else if(i == 0 && j == 1){
            x = x - x_temp;
            z = z + z_temp;
        }else if(i == 1 && j == 0){
            x = x + x_temp;
            z = z - z_temp;
        }else if(i == 1 && j == 1){
            x = x + x_temp;
            z = z + z_temp;
        }
        Location location_temp = new Location(player.getPlayer().getWorld(), x, y, z);
        y = player.getWorld().getHighestBlockYAt(location_temp);
        y++;  */
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
        //square instead of Circle radius is half the side length, no checking for y because of the random TP inside there
        /*
        if(started) {
            Location to = event.getTo();
            if (!to.getWorld().equals(getWarp().getWorld())) {
                event.getPlayer().teleport(getWarp(), TeleportCause_FORCE);
                PluginData.getMessageUtil().sendErrorMessage(event.getPlayer(), "You can't go to another world while in this game.");
                return;
            }
            Location from = event.getFrom();
            Location warp = getWarp();
            if ((to.getX() > (warp.getX() + allowedRadius(event.getPlayer())) || to.getZ() > (warp.getZ() + allowedRadius(event.getPlayer()))) || (to.getX() < (warp.getX() - allowedRadius(event.getPlayer())) || to.getZ() < (warp.getZ() - allowedRadius(event.getPlayer())))) {

                if(!leaveMessaged.contains(event.getPlayer().getUniqueId())) {
                    sendLeaveNotAllowed(event.getPlayer());
                    final UUID uuid = event.getPlayer().getUniqueId();
                    leaveMessaged.add(uuid);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            leaveMessaged.remove(uuid);
                        }
                    }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 200);
                }
                Vector vel = to.toVector().subtract(event.getFrom().toVector());
                Vector radial = event.getPlayer().getLocation().toVector().subtract(getWarp().toVector());
                Vector tangential = new Vector(radial.getZ(),radial.getY(), -radial.getX());
                tangential = tangential.multiply(1/tangential.length());
                double dot = tangential.dot(vel);
                tangential = tangential.multiply(tangential.dot(vel));
                Location newTo = new Location(from.getWorld(),
                        from.getX()+tangential.getX(),
                        from.getY()+tangential.getY(),
                        from.getZ()+tangential.getZ(),
                        to.getYaw(), to.getPitch());
                Vector radialOld = radial.clone();
                Vector radialNorm = radial.multiply(1/radial.length()).clone();
                if((newTo.getX() > (warp.getX() + allowedRadius(event.getPlayer())) || newTo.getZ() > (warp.getZ() + allowedRadius(event.getPlayer()))) || (newTo.getX() < (warp.getX() - allowedRadius(event.getPlayer())) || newTo.getZ() < (warp.getZ() - allowedRadius(event.getPlayer())))) {
                    radial = radial.multiply(allowedRadius(event.getPlayer()));
                    radial = radial.subtract(radialNorm.multiply(0.01));
                    radial = radial.subtract(radialOld);
                    newTo = newTo.add(radial);
                }
                final Player play = event.getPlayer();
                if(!newTo.getBlock().isEmpty()) {
                    newTo.setY(newTo.getBlockY()+1);
                }
                final Location loc = newTo.clone();
                radialNorm = radialNorm.multiply(-0.4);
                //radialNorm.setY(0);
                if(!loc.clone().add(radialNorm.clone().multiply(3)).getBlock().isEmpty()) {
                    radialNorm.setY(0);
                }
                if(!loc.clone().add(radialNorm.clone().multiply(3)).getBlock().isEmpty()) {
                    radialNorm = new Vector(0,0,0);
                }
                final Vector push = radialNorm;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        play.teleport(loc, TeleportCause_FORCE);
                        play.setVelocity(push);
                    }
                }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 1);
            }
        }

         */
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
        /*
        if(!isPlayerInRound()) {
            for (Player player : getOnlinePlayers()) {

                Plugin plugin = MiniGamesPlugin.getPluginInstance();
                for (Player player2 : getOnlinePlayers()) {
                    player.showPlayer(plugin, player2);
                }
            }
        }
         */
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

    private void sendReminder(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player,"forget to set the number of rounds [/game setrounds x] . Default is 5.");
        PluginData.getMessageUtil().sendInfoMessage(player,"Don´t forget to set the area of warps [/game setarea x] . Default is a = all.");
        PluginData.getMessageUtil().sendInfoMessage(player,"Do /game ready when you are done or have nothing done.");
    }

    private void sendLeaveNotAllowed(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "You are not allowed to leave game area.");
    }

}
