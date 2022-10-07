/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.highscores.gameWinHighscore;
import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class AbstractGame {
    
    protected final static TeleportCause TeleportCause_WARP = TeleportCause.NETHER_PORTAL;
   
    protected final static TeleportCause TeleportCause_FORCE = TeleportCause.END_PORTAL;
    
    private final String name;
    
    private boolean announced = false;
    
    private OfflinePlayer manager;

    private final GameType type;
    
    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> bannedPlayers = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final List<UUID> invitedPlayers = new ArrayList<>();
    private final List<UUID> leaveMessaged = new ArrayList<>();
    
    public final Map<UUID,GameMode> playerPreviousMode = new HashMap<>();
    
    private Location warp = null;
    //private boolean warpAllowed = true;
    //private boolean spectateAllowed = true;
    //private boolean privat = false;
    //private boolean flightAllowed = true;
    //private boolean teleportAllowed = true;
    private boolean gm3Allowed = false;
    private boolean gm2Forced = false;
    //private boolean Collision = true;
    
    private final GameScoreboard board;
    private final Team team;

    private static final Map<String,Boolean> toggleConfig = new HashMap<>();

    //flight|teleport|join|warp|spectate|collision|invisible|signs|glow
    //flight|teleport|join|warp|save|collision|invisible|signs|glow
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
    
    private boolean managerOnlineLastTime = true; //for cleanup task

    private gameWinHighscore winHighscore;

    public AbstractGame(Player manager, String name, GameType type, GameScoreboard board) {
        this.name = name;
        this.manager = manager;
        this.board = board;
        this.type = type;
        this.team = board.getScoreboard().registerNewTeam("noCollision");
        team.setCanSeeFriendlyInvisibles(true);
        if(manager!=null) {
            if(type != GameType.GEO_GUESSR) {
                warp = manager.getLocation();
            }
            if(type == GameType.HIDE_AND_SEEK || type == GameType.WEREWOLF){
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
            gameWinHighscore winHighscore = new gameWinHighscore();
            this.winHighscore = winHighscore;
            manager.setScoreboard(getBoard().getScoreboard());
            BukkitRunnable cleanupTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if(!getManager().isOnline()) {
                        if(!managerOnlineLastTime) {
                            end(null);
                            cancel();
                        }
                        else {
                            managerOnlineLastTime = false;
                        }
                    }
                    else {
                        managerOnlineLastTime = true;
                    }
                }};
            cleanupTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 3000, 3000);
        }
    }

    //TODO:
    //config inv for allow/deny
    //command for that
    //rework glow command -> allow/deny
    
    public void end(Player sender) {
        sendGameEndMessage(sender);    
        for(Player player: getOnlinePlayers()) {
                removePlayer(player); 
            }
            PluginData.removeGame(this);
    }
    
    public int countOnlinePlayer() {
        return getOnlinePlayers().size();
    }
    
    public void setManager(OfflinePlayer manager) {
        if(manager != null && PlayerUtil.getOnlinePlayer(manager)!=null) {
            Player oldManager = PlayerUtil.getOnlinePlayer(manager);
            if(!PluginData.isInGame(oldManager)) {
                oldManager.setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
            }
        }
        this.manager = manager;
    }
    
    public OfflinePlayer getPlayer(String name) {
        Player onlinePlayer = Bukkit.getPlayer(name);
        if(onlinePlayer!=null && players.contains(onlinePlayer.getUniqueId())) {
            return onlinePlayer;
        }
        for(UUID player : players) {
            if(Bukkit.getOfflinePlayer(player).getName().equalsIgnoreCase(name)) {
                return Bukkit.getOfflinePlayer(player);
            }
        }
        return null;
    }
    
    public OfflinePlayer getBannedPlayer(String name) {
        for(UUID player : bannedPlayers) {
            if(Bukkit.getOfflinePlayer(player).getName().equalsIgnoreCase(name)) {
                return Bukkit.getOfflinePlayer(player);
            }
        }
        return null;
    }
    
    public List<Player> getOnlinePlayers() {
        List<Player> online = new ArrayList<>();
        for(UUID player : players) {
            Player onlinePlayer = Bukkit.getPlayer(player);
            if(onlinePlayer!=null) {
                online.add(onlinePlayer);
            }
        }
        return online;
    }
    
    public void addPlayer(Player player) {
        if(!toggleConfig.get("flight")) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        if(gm2Forced) {
            playerPreviousMode.put(player.getUniqueId(), player.getGameMode());
            player.setGameMode(GameMode.ADVENTURE);
        }
        if(!gm3Allowed && player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setGameMode(GameMode.SURVIVAL);
        }
        players.add(player.getUniqueId());
        getBoard().incrementPlayer();
        player.setScoreboard(board.getScoreboard());
        team.addEntry(player.getName());
    }
    
    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());
        player.setScoreboard(getBoard().getScoreboard());
    }
    
    public void removeSpectator(Player player) {
        if(spectators.remove(player.getUniqueId())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
    
    public boolean isSpectating(Player player) {
        return spectators.contains(player.getUniqueId());
    }
    
    public void removePlayer(OfflinePlayer player) {
        if(players.remove(player.getUniqueId())) {
            getBoard().decrementPlayer();
            Player onlinePlayer = PlayerUtil.getOnlinePlayer(player);
            if(onlinePlayer!=null) {
                if(!PlayerUtil.isSame(onlinePlayer,manager)) {
                    onlinePlayer.setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
                }
                onlinePlayer.setGlowing(false);
                if(gm2Forced) {
                    onlinePlayer.setGameMode(playerPreviousMode.get(onlinePlayer.getUniqueId()));
                    playerPreviousMode.remove(onlinePlayer.getUniqueId());
                }
            }
        }
    }
    
    public boolean isBanned(OfflinePlayer player) {
        return bannedPlayers.contains(player.getUniqueId());
    }
    
    public boolean isInGame(OfflinePlayer player) {
        return players.contains(player.getUniqueId());
    }

    public void setBanned(OfflinePlayer player) {
        bannedPlayers.add(player.getUniqueId());
    }
        
    public void setUnbanned(OfflinePlayer player) {
        bannedPlayers.remove(player.getUniqueId());
    }
        
    public void playerJoinServer(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(board.getScoreboard());
        getBoard().incrementPlayer();
        if(!toggleConfig.get("flight")) {
            event.getPlayer().setFlying(false);
            event.getPlayer().setAllowFlight(false);
        }
    }
    
    public void playerLeaveServer(PlayerQuitEvent event) {
        event.getPlayer().setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
        getBoard().decrementPlayer();
    }
    
    public int allowedRadius(Player player) {
        return Integer.MAX_VALUE;
    }
    
    public void playerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        if(!to.getWorld().equals(getWarp().getWorld())) {
            event.getPlayer().teleport(getWarp(), TeleportCause_FORCE);
            PluginData.getMessageUtil().sendErrorMessage(event.getPlayer(),"You can't go to another world while in this game.");
            return;
        }
        Location from = event.getFrom();
        if(to.distance(getWarp())>allowedRadius(event.getPlayer())) {
            //event.setCancelled(true);
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
            if(newTo.distance(getWarp())>allowedRadius(event.getPlayer())) {
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



    public void playerTeleport(PlayerTeleportEvent event) {
        if((!toggleConfig.get("teleport") && !event.getCause().equals(TeleportCause_FORCE))
                             && !event.getCause().equals(PlayerTeleportEvent.TeleportCause.UNKNOWN)) {
            event.setCancelled(true);
            sendTeleportNotAllowed(event.getPlayer());
        }
    }
    
    public void playerToggleFlight(PlayerToggleFlightEvent event) {
        if(!toggleConfig.get("flight")) {
            event.getPlayer().setFlying(false);
            event.getPlayer().setAllowFlight(false);
            event.setCancelled(true);
            sendFlightNotAllowed(event.getPlayer());
        }
    }
    
    public void playerChangeGameMode(PlayerGameModeChangeEvent event) {
        if(gm2Forced) {
            event.setCancelled(true);
            return;
        }
        if(!gm3Allowed && event.getNewGameMode().equals(GameMode.SPECTATOR)) {
            event.setCancelled(true);
            sendGm3NotAllowed(event.getPlayer());
        }
    }

    public void setGlow(boolean allowed){
        for(Player player: getOnlinePlayers()){
            player.setGlowing(allowed);
        }
    }

    public void playerInteract(PlayerInteractEntityEvent event){
        event.setCancelled(true);
    }

    public void onClick(InventoryClickEvent event){
       // event.setCancelled(false);
    }

    public void checkThrow(PlayerInteractEvent event) {}

    public void playerDamaged(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }
    
    public void forceTeleport(Player player, Location loc) {
        player.teleport(loc, TeleportCause_FORCE);
    }
    
    public void warp(Player player, Location loc) {
        player.teleport(loc, TeleportCause_WARP);
    }

    public boolean joinAllowed() {
        return announced;
    }
    
    public void announceGame() {
        announced = true;
        if(!isPrivat()) {
            sendAnnounceGameMessage();
        }
    }
    
    public boolean isInvited(OfflinePlayer player) {
        return invitedPlayers.contains(player.getUniqueId());
    }
    
    public void invite(OfflinePlayer player) {
        invitedPlayers.add(player.getUniqueId());
    }
    
    public void setFlightAllowed(boolean allowed) {
        if(toggleConfig.get("flight") && !allowed)  {
            for(Player player : getOnlinePlayers()) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        }
        toggleConfig.replace("flight",allowed);
    }

    public void setCollision(boolean allowed) {
        if (!toggleConfig.get("collision") && allowed) { // false && true
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            }
        else if(toggleConfig.get("collision") && !allowed){ // true && false
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
        // Collision is by default on ALWAYS
        toggleConfig.replace("collision",allowed);
    }

    public void setSpectateAllowed(boolean allowed) {
        if(toggleConfig.get("spectate") && !allowed)  {
            List<UUID> copyOfSpectators = new ArrayList<>();
            copyOfSpectators.addAll(spectators);
            for(UUID uuid : copyOfSpectators) {
                Player player = Bukkit.getPlayer(uuid);
                if(player!=null) {
                    removeSpectator(player);
                    sendNoMoreSpectatingMessage(player);
                }
            }
        }
        toggleConfig.replace("spectate",allowed);
    }
    
    public String getGameChatTag(Player player) {
        if(PluginData.isManager(player)) {
            return ChatColor.DARK_AQUA + "<Manager "; 
        }
        else {
            return ChatColor.BLUE + "<Participant "; 
        }
    }
    
    protected void sendAnnounceGameMessage() {
        Plugin connectPlugin = Bukkit.getPluginManager().getPlugin("MCME-Connect");
        Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if(player !=null && connectPlugin != null && connectPlugin.isEnabled()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF("ALL");
            String message = PluginData.getMessageUtil().STRESSED+manager.getName()
                    + PluginData.getMessageUtil().INFO+" started a new "
                    + PluginData.getMessageUtil().STRESSED+getType().toString()
                    + PluginData.getMessageUtil().INFO+" game. "
                    + "To play that game, type in chat: "
                    + PluginData.getMessageUtil().STRESSED+"/game join "+getName()
                    +  PluginData.getMessageUtil().INFO+" (If you are at another world you need to first do "
                    +  PluginData.getMessageUtil().STRESSED+"/"+player.getWorld().getName()+ PluginData.getMessageUtil().INFO+")";
            out.writeUTF(message);
            player.sendPluginMessage(MiniGamesPlugin.getPluginInstance(), "BungeeCord", out.toByteArray());
            Logger.getGlobal().info("Bungee Broadcast sent! "+message);
        } else {
            PluginData.getMessageUtil().sendBroadcastMessage(new FancyMessage(MessageType.INFO, PluginData.getMessageUtil()).
                    addClickable(PluginData.getMessageUtil().STRESSED + manager.getName()
                            + PluginData.getMessageUtil().INFO + " started a new "
                            + PluginData.getMessageUtil().STRESSED + getType().toString()
                            + PluginData.getMessageUtil().INFO + " game. "
                            + "To play that game, "
                            + PluginData.getMessageUtil().STRESSED + "click here "
                            + PluginData.getMessageUtil().INFO + "or type in chat: /game join " + getName(), "/game join " + getName()));
        }
    }
    
    public void sendGameEndMessage(Player sender) {
        GameChatUtil.sendAllInfoMessage(sender, this, "The game "+ getName()+" ended.");
    }

    private void sendLeaveNotAllowed(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "You are not allowed to leave game area.");
    }

    private void sendTeleportNotAllowed(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "You are not allowed to teleport in this game.");
    }

    private void sendFlightNotAllowed(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "You are not allowed to fly in this game.");
    }

    private void sendGm3NotAllowed(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "You are not allowed to switch to spectator mode in this game.");
    }

    private void sendNoMoreSpectatingMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Spectating in game '"+PluginData.getMessageUtil().STRESSED+getName()
                                           +PluginData.getMessageUtil().INFO+"' is no longer allowed.");
    }

    public String getName() {
        return name;
    }

    public boolean isAnnounced() {
        return announced;
    }

    public OfflinePlayer getManager() {
        return manager;
    }

    public GameType getType() {
        return type;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Location getWarp() {
        return warp;
    }

    public void setWarp(Location loc){this.warp = loc;}

    public boolean isWarpAllowed() {
        return toggleConfig.get("warp");
    }

    public void setWarpAllowed(boolean warpAllowed) {
        toggleConfig.replace("warp",warpAllowed);
    }

    public boolean isSpectateAllowed() {
        return toggleConfig.get("spectate");
    }

    public boolean isPrivat() {
        return toggleConfig.get("privat");
    }

    public void setPrivat(boolean privat) {
        toggleConfig.replace("privat",privat);
    }

    public void setThrowable(boolean bool){
        toggleConfig.replace("throwable",bool);
    }

    public boolean getThrowable(){ return toggleConfig.get("trowable"); }

    public void setTPSave(boolean bool) { toggleConfig.replace("save",bool); }

    public boolean getTPSave(){ return toggleConfig.get("save"); }

    public void setInvisible(boolean bool){ toggleConfig.replace("invisible",bool); }

    public boolean getInvisible(){ return toggleConfig.get("invisible"); }

    public void setPoints(boolean bool){ toggleConfig.replace("points",bool); }

    public boolean getPoints(){ return toggleConfig.get("points"); }

    public void setSigns(boolean bool){ toggleConfig.replace("signs",bool); }

    public boolean getSigns(){ return toggleConfig.get("signs"); }

    public boolean isFlightAllowed() {
        return toggleConfig.get("flight");
    }

    public boolean isTeleportAllowed() {
        return toggleConfig.get("teleport");
    }

    public void setTeleportAllowed(boolean teleportAllowed) {
        toggleConfig.replace("teleport",teleportAllowed);
    }

    public boolean isGm3Allowed() {
        return gm3Allowed;
    }

    public void setGm3Allowed(boolean gm3Allowed) {
        this.gm3Allowed = gm3Allowed;
    }

    public boolean isGm2Forced() {
        return gm2Forced;
    }

    public void setGm2Forced(boolean gm2Forced) {
        this.gm2Forced = gm2Forced;
    }

    public gameWinHighscore getWinHighscore(){
        return winHighscore;
    }

    public GameScoreboard getBoard() {
        return board;
    }
}
