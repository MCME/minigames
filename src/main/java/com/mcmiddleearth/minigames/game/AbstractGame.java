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
import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
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
    private boolean warpAllowed = true;
    private boolean spectateAllowed = true;
    private boolean privat = false;
    private boolean flightAllowed = true;
    private boolean teleportAllowed = true;
    private boolean gm3Allowed = false;
    private boolean gm2Forced = false;
    
    private final GameScoreboard board;
    
    private boolean managerOnlineLastTime = true; //for cleanup task
    
    public AbstractGame(Player manager, String name, GameType type, GameScoreboard board) {
        this.name = name;
        this.manager = manager;
        this.board = board;
        this.type = type;
        if(manager!=null) {
            warp = manager.getLocation();
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
        if(!flightAllowed) {
            player.setFlying(false);
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
        if(!flightAllowed) {
            event.getPlayer().setFlying(false);
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
        if((!teleportAllowed && !event.getCause().equals(TeleportCause_FORCE))
                             && !event.getCause().equals(PlayerTeleportEvent.TeleportCause.UNKNOWN)) {
            event.setCancelled(true);
            sendTeleportNotAllowed(event.getPlayer());
        }
    }
    
    public void playerToggleFlight(PlayerToggleFlightEvent event) {
        if(!flightAllowed) {
            event.getPlayer().setFlying(false);
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
        if(this.flightAllowed && !allowed)  {
            for(Player player : getOnlinePlayers()) {
                player.setFlying(false);
            }
        }
        flightAllowed = allowed;
    }
    
    public void setSpectateAllowed(boolean allowed) {
        if(this.spectateAllowed && !allowed)  {
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
        spectateAllowed = allowed;
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

    public boolean isWarpAllowed() {
        return warpAllowed;
    }

    public void setWarpAllowed(boolean warpAllowed) {
        this.warpAllowed = warpAllowed;
    }

    public boolean isSpectateAllowed() {
        return spectateAllowed;
    }

    public boolean isPrivat() {
        return privat;
    }

    public void setPrivat(boolean privat) {
        this.privat = privat;
    }

    public boolean isFlightAllowed() {
        return flightAllowed;
    }

    public boolean isTeleportAllowed() {
        return teleportAllowed;
    }

    public void setTeleportAllowed(boolean teleportAllowed) {
        this.teleportAllowed = teleportAllowed;
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

    public GameScoreboard getBoard() {
        return board;
    }
}
