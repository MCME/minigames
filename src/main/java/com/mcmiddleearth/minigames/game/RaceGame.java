/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.highscores.raceHighscoreAbstract;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.raceCheckpoint.CheckpointManager;
import com.mcmiddleearth.minigames.scoreboard.RaceGameScoreboard;
import com.mcmiddleearth.pluginutil.TitleUtil;
import org.bukkit.*;
//import org.bukkit.boss.BarColor;
//import org.bukkit.boss.BarStyle;
//import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGame extends AbstractGame {
    
    private final CheckpointManager checkpointManager = new CheckpointManager(getName());

    private boolean started = false;
    private boolean steady = false;
    private int finished = 0;
    private boolean autoShow = true;
    private int timer = 10;
    
    private BukkitRunnable goTask; 
    
    private List<Location> cageLocations = new ArrayList<>();
    
    private final Map<UUID,Integer> nextCheckpoints = new HashMap<>();

    private final Map<UUID,ItemStack> helmet_save = new HashMap<>();

    //private final Map<UUID,BossBar> bar = new HashMap<>();

    private Map<UUID,Location> tp_save = new HashMap<>();

    private List<Player> save = new ArrayList<>();

    //private boolean save_allowed = true;

    private String raceName = "temporaryRace";

    private raceHighscoreAbstract highscore;

    //Visibility default off because its still buggy for non donors
    //private boolean invisibile_allowed = true;
    
    public RaceGame(Player manager, String name) {
        super(manager, name, GameType.RACE, new RaceGameScoreboard());
        setFlightAllowed(false);
        setTeleportAllowed(false);
        setGm2Forced(true);
        setCollision(false);
        setGlow(false);
        ((RaceGameScoreboard)getBoard()).init(this);
    }

    public void setHighscore(){
        raceHighscoreAbstract highscore = new raceHighscoreAbstract(raceName, getManager().getPlayer());
        this.highscore = highscore;
        Map.Entry<String,Object> no1 = highscore.getHighscore();
        OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(no1.getKey()));
        ((RaceGameScoreboard)getBoard()).addHighscore(op.getName(),(int) no1.getValue());
    }

    public void resetHighscore(Player player){
        highscore.resetHighscore(player.getUniqueId());
        sendReset(player);
    }

    public void getStats(Player player){
        int pb = highscore.getPB(player.getUniqueId());
        sendPB(player,pb);
        Map<String,Object> top5 = highscore.getTop5();
        sendTop5(player,String.valueOf(top5));
    }

    /*
    public void setSave(boolean allowed){
        save_allowed = allowed;
    }
     */

    //public void setInvisbile(boolean allowed){ invisibile_allowed = allowed;}

    @Override
    public void playerMove(PlayerMoveEvent event) {
        if(started) {
            if(checkpointManager.getFinish().isCheckLocation(event.getPlayer().getLocation())
                    && getNextCheckpoint(event.getPlayer())==checkpointManager.getCheckpoints().size()+1) {
                incrementCheckpoint(event.getPlayer());
                event.getPlayer().playEffect(checkpointManager.getFinish().getLocation(),Effect.CLICK2,0);
                if(autoShow) {
                    ((RaceGameScoreboard)getBoard()).showFinish();
                }
                ((RaceGameScoreboard)getBoard()).finish(event.getPlayer().getName());
                finished ++;
                TitleUtil.showTitle(event.getPlayer(),ChatColor.GOLD+"FINISH", 
                                            "You are placed "+getPlace()+".");
                int currentTime = ((RaceGameScoreboard)getBoard()).getTime(event.getPlayer().getName());
                if(finished==1) {
                    Map.Entry<String,Object> no1 = highscore.getHighscore();
                    getWinHighscore().setRaceWin(event.getPlayer().getUniqueId());
                    if((int) no1.getValue() > currentTime){
                        TitleUtil.showTitleAll(getOnlinePlayers(),event.getPlayer(),
                                ChatColor.BLUE+event.getPlayer().getName(),"broke the record of the race and won.");
                        newRecord(event.getPlayer());
                        highscore.setHighscore(event.getPlayer().getUniqueId(),currentTime);
                    }else{
                        TitleUtil.showTitleAll(getOnlinePlayers(),event.getPlayer(),
                                ChatColor.BLUE+event.getPlayer().getName(),"won the race.");
                    }
                }
                if(getInvisible()) {
                    event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                    if(helmet_save.containsKey(event.getPlayer().getUniqueId())){
                        event.getPlayer().getInventory().setHelmet(helmet_save.get(event.getPlayer().getUniqueId()));
                        helmet_save.remove(event.getPlayer().getUniqueId());
                    }
                }
                int pb = highscore.getPB(event.getPlayer().getUniqueId());
                if(currentTime < pb){
                    highscore.setPB(event.getPlayer().getUniqueId(),currentTime);
                    sendNewPB(event.getPlayer(),currentTime);
                }
                //bar.get(event.getPlayer().getUniqueId()).setProgress(0.0);
                //bar.get(event.getPlayer().getUniqueId()).setTitle("Race: Finish");
            }
            for(Checkpoint check:checkpointManager.getCheckpoints()) {
                int checkId = checkpointManager.getId(check);
                if(check.isCheckLocation(event.getPlayer().getLocation())
                        && checkId == getNextCheckpoint(event.getPlayer())) {
                    tp_save.replace(event.getPlayer().getUniqueId(),event.getPlayer().getLocation());
                    incrementCheckpoint(event.getPlayer());
                    //bar.get(event.getPlayer().getUniqueId()).setProgress(bar.get(event.getPlayer().getUniqueId()).getProgress()+(double)(1/checkpointManager.getCheckpoints().size()));
                    //bar.get(event.getPlayer().getUniqueId()).setTitle("Race: Checkpoint "+checkId);
                    PluginData.getMessageUtil().sendInfoMessage(event.getPlayer(),"You reached checkpoint "+checkId+".");
                    event.getPlayer().playEffect(check.getLocation(),Effect.CLICK2,0);
                    if(autoShow) {
                        ((RaceGameScoreboard)getBoard()).showCheckpoint(checkId);
                    }
                    ((RaceGameScoreboard)getBoard()).
                                chechpointReached(event.getPlayer().getName(), checkId);

                    //Check what happens when last checkpoint is reached
                    LinkedList<Checkpoint> length_check = checkpointManager.getCheckpoints();
                    if(length_check.size() > checkId) {
                        Checkpoint check_compass = checkpointManager.getCheckpoint(checkId + 1);
                        event.getPlayer().setCompassTarget(check_compass.getLocation());
                    } else {
                        Checkpoint finish = checkpointManager.getFinish();
                        event.getPlayer().setCompassTarget(finish.getLocation());
                    }
                }
            }
        }else{   //That people don´t run away before the start
            super.playerMove(event);
        }
    }

    @Override
    public int allowedRadius(Player player){
        return 10;
    }

    private String getPlace() {
        if((finished % 10)==1) {
            return finished+"st";
        }
        if((finished % 10)==2) {
            return finished+"nd";
        }
        if((finished % 10)==3) {
            return finished+"rd";
        }
        return finished+"th";
    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        int i = highscore.getPB(player.getUniqueId());
        forceTeleport(player,getWarp());
        ((RaceGameScoreboard) getBoard()).addPlayer(player.getName());
        if(!save.contains(player)) {   //That it doesnt bug when a Player joins, leaves and joins again
            tp_save.put(player.getUniqueId(),getWarp());
            save.add(player);
        }

        if(player.getInventory().getChestplate() != null){
            Material chestplate = player.getInventory().getChestplate().getType();
            if(chestplate == Material.ELYTRA) {
                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                player.getInventory().addItem(new ItemStack(chestplate));
                sendElytraRemoved(player);
            }
        }
        player.getInventory().addItem(new ItemStack(Material.COMPASS,1));
    }

    @Override
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);
        if(getInvisible()) {
            if (player.isOnline()) {
                Player player_on = (Player) player;
                player_on.removePotionEffect(PotionEffectType.INVISIBILITY);
                if(helmet_save.containsKey(player.getUniqueId())){
                    ((Player) player).getInventory().setHelmet(helmet_save.get(player.getUniqueId()));
                    helmet_save.remove(player.getUniqueId());
                }
            }
        }
        if(save.contains((Player) player)){
            tp_save.remove((Player) player);
            save.remove((Player) player);
        }
    }

    @Override
    public void end(Player player) {
        checkpointManager.deleteCheckpoints();
        if(steady) cagePlayer(false);
        super.end(player);
    }
    
    @Override
    public boolean joinAllowed() {
        return super.joinAllowed() && !started;
    }
    
    @Override
    public void playerTeleport(PlayerTeleportEvent event) {
        super.playerTeleport(event);
        if(started && event.getCause().equals(TeleportCause_WARP)) {
            event.setCancelled(true);
        }
    }
    
    public void steady() {
        steady = true;
        started = true;
        if(getInvisible()) {
            for (Player player : getOnlinePlayers()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
                ItemStack helmet = player.getInventory().getHelmet();
                if(helmet != null) {
                    player.getInventory().setHelmet(new ItemStack(Material.AIR));
                    helmet_save.put(player.getUniqueId(),helmet);
                    sendHelmetRemoved(player);
                }
            }
        }
        resetNextCheckpoints();
        cageLocations = getCageLocations(checkpointManager.getStart());
        cagePlayer(true);
        final String title = ChatColor.RED+"start in";
        TitleUtil.showTitleAll(getOnlinePlayers(), null, title,"",20,300,0);
        timer = 11;
        goTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(timer>1) {
                    timer--;
                    TitleUtil.showTitleAll(getOnlinePlayers(), null, null, timer+"",0,300,0);
                }
                else {
                    cancel();
                    TitleUtil.showTitleAll(getOnlinePlayers(), null, ChatColor.GREEN+"GO","",0,30,60);
                    go();
                }
            }
        
            @Override
            public void cancel() {
                super.cancel();
            }};
        goTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }
    
    public void go() {
        steady = false;
        cagePlayer(false);
        ((RaceGameScoreboard)getBoard()).startRace();
    }
    
    public void stop() {
        if(goTask!= null) {
            goTask.cancel();
        }
        if(steady) cagePlayer(false);
        started = false;
        steady = false;
        finished = 0;
        ((RaceGameScoreboard)getBoard()).stopRace();
    }

    public void TpToStart(Player player){
        if (!started) {
            Checkpoint start = checkpointManager.getStart();
            Location teleportLoc = start.getLocation();
            forceTeleport(player,teleportLoc);
            setWarp(teleportLoc);
        }
    }

    public void tp_Save(Player player) {
        if (getTPSave()) {
            if (save.contains(player)) {
                if (tp_save.containsKey(player.getUniqueId())) {
                    forceTeleport(player, tp_save.get(player.getUniqueId()));
                    save.remove(player);
                }
            } else {
                sendNoSaveLeft(player);
            }
        } else {
            sendNotAllowed(player);
        }
    }



    private void cagePlayer(boolean cage) {
        Checkpoint start = checkpointManager.getStart();
        List<Location> checkList = start.getCheckLocList();
        if(!cageLocations.isEmpty()) {
            ListIterator<Location> listIterator = checkList.listIterator();
            for(Player player: getOnlinePlayers()) {
                if(cage) {
                    Location teleportLoc = listIterator.next();
                    teleportLoc.setX(teleportLoc.getBlockX()+0.5);
                    teleportLoc.setY(teleportLoc.getBlockY()+0.5);
                    teleportLoc.setZ(teleportLoc.getBlockZ()+0.5);
                    teleportLoc.setYaw(start.getLocation().getYaw());
                    forceTeleport(player,teleportLoc);
                }
                for(Location loc: cageLocations) {
                    Material material;
                    byte value;
                    if(cage) {
                        if(loc.getBlock().isPassable()) {
                            material=Material.BARRIER;
                        }
                        else {
                            material=Material.BEDROCK;
                        }
                        value = 0;
                    }
                    else {
                        material=loc.getBlock().getType();
                        value = loc.getBlock().getData();
                    }
                    player.sendBlockChange(loc, material, value);
                }
                if(!listIterator.hasNext()) {
                    listIterator=checkList.listIterator();
                }
                listIterator.next();
                if(!listIterator.hasNext()) {
                    listIterator=checkList.listIterator();
                }


                if(checkpointManager.getCheckpoints().size() < 1){
                    Checkpoint finish = checkpointManager.getFinish();
                    player.setCompassTarget(finish.getLocation());
                }else{
                    Checkpoint first = checkpointManager.getCheckpoint(1);
                    player.setCompassTarget(first.getLocation());
                }


            }
        }
    }

    private List<Location> getCageLocations(Checkpoint check) {
        List<Location> blockLocations = new ArrayList<>();
        List<Location> checkLocs = check.getCheckLocList();
        for(Location loc : checkLocs) {
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0,-1, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 2, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative(-1, 0, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 1, 0, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 0,-1).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 0, 1).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative(-1, 1, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 1, 1, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 1,-1).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 1, 1).getLocation());
        }
        return blockLocations;
    }
    
    public void showStart() {
        autoShow = false;
        ((RaceGameScoreboard) getBoard()).showStart();
    }
    public void showFinish() {
        autoShow = false;
        ((RaceGameScoreboard) getBoard()).showFinish();
    }
    public void showCheckpoint(int checkId) {
        autoShow = false;
        ((RaceGameScoreboard) getBoard()).showCheckpoint(checkId);
    }
    
    public void showAuto() {
        autoShow = true;
    }
    
    private void resetNextCheckpoints() {
        nextCheckpoints.clear();
        for(UUID player: getPlayers()) {
            nextCheckpoints.put(player, 1);
        }
    }
    
    private int getNextCheckpoint(OfflinePlayer player) {
        for(UUID search: nextCheckpoints.keySet()) {
            if(player.getUniqueId().equals(search)) {
                return nextCheckpoints.get(search);
            }
        }
        nextCheckpoints.put(player.getUniqueId(), 1);
        return 1;
    }
    
    private void incrementCheckpoint(OfflinePlayer player) {
        for(UUID search: nextCheckpoints.keySet()) {
            if(player.getUniqueId().equals(search)) {
                nextCheckpoints.put(search, nextCheckpoints.get(search)+1);
                return;
            }
        }
        nextCheckpoints.put(player.getUniqueId(), 2);
    }
    
    public boolean playerRacing() {
        for(Player player: getOnlinePlayers()) {
            if(getNextCheckpoint(player)<checkpointManager.getCheckpoints().size()+2) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getGameChatTag(Player player) {
        if(player.getUniqueId().equals(UUID.fromString("b8d1ce5c-2b38-428c-9bb8-c8ee6ad58c4b")))
            return ChatColor.DARK_RED + "<Game Master ";
        if(PluginData.isManager(player)) {
            return ChatColor.DARK_AQUA + "<Manager "; 
        }
        else {
            return ChatColor.BLUE + "<Racer: "; 
        }
    }
    
    public boolean hasStart() {
        return checkpointManager.getStart()!=null;
    }
    public boolean hasFinish() {
        return checkpointManager.getFinish()!=null;
    }

    public CheckpointManager getCheckpointManager() {
        return checkpointManager;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isSteady() {
        return steady;
    }

    private void sendNoSaveLeft(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "You don´t have a save left.");
    }

    private void sendHelmetRemoved(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You will get your helmet back after the race.");
    }

    private void sendElytraRemoved(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "The elytra was put into your inventory.");
    }

    private void sendNotAllowed(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "This is not allowed.");
    }

    private void sendNewPB(Player player, Integer PB){
        player.playEffect(player.getLocation(),Effect.FIREWORK_SHOOT,2);
        PluginData.getMessageUtil().sendInfoMessage(player, "You got a new personal best with "+PB);
    }

    private void sendPB(Player player, Integer PB){
        PluginData.getMessageUtil().sendInfoMessage(player, "Your personal best for this race is "+PB);
    }

    private void sendTop5(Player player, String top5){
        PluginData.getMessageUtil().sendInfoMessage(player, "These are the Top 5 "+top5);
    }

    private void newRecord(Player player){
        PluginData.getMessageUtil().sendInfoMessage(player,"You broke the record of the race.");
    }

    private void sendReset(Player player){
        PluginData.getMessageUtil().sendInfoMessage(player,"Record of the race was reset.");
    }

    public void setRaceName(String name){
        this.raceName = name;
    }

    public String getRaceName(){
        return this.raceName;
    }
}
