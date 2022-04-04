package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.scoreboard.ManhuntGameScoreboard;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.DynmapUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.TitleUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ManhuntGame extends AbstractGame implements Listener {

    private final int seekerCageRadius = 5;
    private final int defaultRadius = 10;
    private final int defaultHideTimeSeconds = 60;
    private final int defaultSeekTimeSeconds = 300;
    private final int revealDistance = 1;

    private int seekTime = defaultSeekTimeSeconds;
    private int hideTime = defaultHideTimeSeconds;
    private int radius;

    private boolean seeking = false;
    private boolean hiding = false;

    public final List<OfflinePlayer> seeker = new ArrayList<>();
    public final List<Player> hiddenPlayers = new ArrayList<>();

    private BukkitRunnable seekTask, stopTask;

    private BossBar bar;

    public ManhuntGame(Player manager, String name){
        super(manager,name,GameType.MANHUNT,new ManhuntGameScoreboard());

        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setTeleportAllowed(false);
        setFlightAllowed(false);
        setGm2Forced(true);
        setCollision(true);
        announceGame();

        BossBar bar = Bukkit.createBossBar(ChatColor.GREEN+"Manhunt", BarColor.WHITE, BarStyle.SOLID);
        bar.setProgress(1.0);
        bar.setVisible(true);
        this.bar = bar;
    }

    public void hiding(int radius) {
        if(radius>0) {
            this.radius = radius;
        }
        else {
            this.radius = defaultRadius;
        }
        this.hiding = true;
        this.seeking = false;

        bar.setTitle(ChatColor.YELLOW+"Manhunt: Hiding");
        bar.setProgress(1.0);

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta_helmet = (LeatherArmorMeta) helmet.getItemMeta();
        meta_helmet.setColor(Color.WHITE);
        helmet.setItemMeta(meta_helmet);

        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta_chest =  (LeatherArmorMeta) chest.getItemMeta();
        meta_chest.setColor(Color.WHITE);
        chest.setItemMeta(meta_chest);

        ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta_legs =  (LeatherArmorMeta) legs.getItemMeta();
        meta_legs.setColor(Color.WHITE);
        legs.setItemMeta(meta_legs);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta_boots =  (LeatherArmorMeta) boots.getItemMeta();
        meta_boots.setColor(Color.WHITE);
        boots.setItemMeta(meta_boots);

        for(OfflinePlayer OP:seeker) {
            Player player = Bukkit.getPlayer(OP.getUniqueId());
            if (player != null) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3));

                player.getInventory().setHelmet(helmet);
                player.getInventory().setChestplate(chest);
                player.getInventory().setLeggings(legs);
                player.getInventory().setBoots(boots);
                player.getInventory().addItem(new ItemStack(Material.CARROT));
            }
        }
        for(Player player : getOnlinePlayers()) {
            if(!seeker.contains(player)) {
                hidePlayer(player);
            }
        }

        String seeker_string = "";
        for(OfflinePlayer op:seeker){
            seeker_string = seeker_string + op.getName() + ", ";
        }

        ((ManhuntGameScoreboard)this.getBoard()).startHiding(seeker_string, hideTime,bar);
        sendStartHideMessage();
        sendRadiusMessage();
        Location loc = getWarp().clone();
        loc.setPitch(80);
        for(OfflinePlayer op:seeker){
            forceTeleport((Player) op,loc);
        }
        seekTask = new BukkitRunnable() {
            @Override
            public void run() {
                seeking();
            }};
        seekTask.runTaskLater(MiniGamesPlugin.getPluginInstance(), hideTime*20);
    }

    public void seeking() {
        this.hiding = false;
        this.seeking = true;

        bar.setTitle(ChatColor.YELLOW+"Manhunt: Seeking");
        bar.setProgress(1.0);
        for(Player p : getOnlinePlayers()){
            bar.addPlayer(p);
        }
        ((ManhuntGameScoreboard)this.getBoard()).startSeeking(seekTime,bar);

        sendStartSeekingMessage();
        stopTask = new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }};
        stopTask.runTaskLater(MiniGamesPlugin.getPluginInstance(),seekTime*20);
    }

    public void stop() {
        if(seekTask!=null) {
            seekTask.cancel();
        }
        if(stopTask!=null) {
            stopTask.cancel();
        }
        bar.setTitle(ChatColor.YELLOW+"Hide and Seek");
        sendStopSeekingMessage();
        for(Player player : getOnlinePlayers()) {
            if(hiddenPlayers.contains(player)) {
                unhidePlayer(player);
            }
            player.setDisplayName(player.getName());
            player.setGlowing(false);
            forceTeleport(player,getWarp());
            //bar.removePlayer(player);
        }
        this.seeking = false;
        this.hiding = false;
        ((ManhuntGameScoreboard)this.getBoard()).stop();
    }

    private void hidePlayer(Player player) {
        hiddenPlayers.add(player);
        DynmapUtil.hide(player);
        Team team = player.getScoreboard().getTeam("noCollision");
        team.addEntry(player.getName());
    }

    private void unhidePlayer(Player player) {
        hiddenPlayers.remove(player);
        Team team = player.getScoreboard().getTeam("noCollision");
        team.removeEntry(player.getName());
        DynmapUtil.show(player);
        player.setGlowing(false);
    }

    private void revealPlayer(Player player) {
        unhidePlayer(player);
        ((ManhuntGameScoreboard)this.getBoard()).locatePlayer();
        if(hiddenPlayers.isEmpty()) {
            stop();
        }
    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        forceTeleport(player,getWarp());
        bar.addPlayer(player);
    }

    @Override
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);
        if(player.isOnline()){
            Player player_on = (Player) player;
            player_on.setGlowing(false);
            bar.removePlayer(player_on);
        }
        if(seeker.contains(player)) {
            seeker.remove(player);
            Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
            if(onlinePlayer != null){
                onlinePlayer.removePotionEffect(PotionEffectType.SPEED);
                onlinePlayer.removePotionEffect(PotionEffectType.JUMP);
                onlinePlayer.getInventory().setHelmet(new ItemStack(Material.AIR));
                onlinePlayer.getInventory().setChestplate(new ItemStack(Material.AIR));
                onlinePlayer.getInventory().setLeggings(new ItemStack(Material.AIR));
                onlinePlayer.getInventory().setBoots(new ItemStack(Material.AIR));
            }
            if(seeker.isEmpty()){
                stop();
            }
        }
    }

    public void setSeeker(OfflinePlayer player) {
        seeker.add(player);
        ((ManhuntGameScoreboard)getBoard()).setSeeker(player.getName());
    }

    private boolean isHidden(Player player) {
        for(Player search : hiddenPlayers) {
            if(PlayerUtil.isSame(search,player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void playerLeaveServer(PlayerQuitEvent event) {
        super.playerLeaveServer(event);
        Player player = event.getPlayer();
        if(isHidden(player)) {
            revealPlayer(player);
        }
        if(seeker.contains(player)) {
            seeker.remove(player);
            if(seeker.isEmpty()){
                sendSeekerLeavingMessage(player);
                stop();
            }
        }
    }

    @Override
    public int allowedRadius(Player player) {
        if(seeking) {
            return Math.abs(radius);
        }
        if(hiding && seeker.contains(player)) {
            return Math.abs(seekerCageRadius);
        }
        if(hiding) {
            return Math.abs(radius);
        }
        return Math.abs(defaultRadius);
    }

    @Override
    public void playerMove(PlayerMoveEvent event) {
        super.playerMove(event);
        if(hiding && seeker.contains(event.getPlayer())) {
            if(event.getTo().getPitch()<70) {
                Location to = event.getTo().clone();
                to.setPitch(80);
                event.setCancelled(true);
                event.getPlayer().teleport(to, TeleportCause_FORCE);
            }
        }
        if(seeking) {
            if(!seeker.contains(event.getPlayer())) {
                //event.getPlayer().setSneaking(true);
            } else {
                Player[] myList = hiddenPlayers.toArray(new Player[0]);
                for(Player hidden : myList) {
                    if(event.getTo().distance(hidden.getLocation())<revealDistance) {
                        sendPlayerFoundMessage(hidden);
                        revealPlayer(hidden);
                    }
                }
            }
        }
    }

    @Override
    public void playerDamaged(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }
        if(!seeker.contains(event.getDamager())) {
            return;
        }
        ItemStack carrot = new ItemStack(Material.CARROT);
        event.getDamager().sendMessage("Test");
        if(((Player) event.getDamager()).getItemInHand() == carrot){
            Player player = (Player) event.getEntity();
            if(seeking && hiddenPlayers.contains(player)) {
                this.revealPlayer(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!hiddenPlayers.isEmpty()) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.contains(event.getPlayer())) {
                if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
                    if (itemInHand.getType().equals(Material.GHAST_TEAR)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        List<Block> blocks = new ArrayList<>();

        if (!hiddenPlayers.isEmpty() && seeker != null) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.contains(event.getPlayer())) {
                for (int x = getWarp().getBlockX() - radius; x <= getWarp().getBlockX() + radius; x++) {
                    for (int y = getWarp().getBlockY() - radius; y <= getWarp().getBlockY() + radius; y++) {
                        for (int z = getWarp().getBlockZ() - radius; z <= getWarp().getBlockZ() + radius; z++) {
                            blocks.add(getWarp().getWorld().getBlockAt(x, y, z));
                        }
                    }
                }

                if (blocks.contains(event.getBlock())) {
                    if (!getPlayers().contains(event.getPlayer().getUniqueId())) {
                        PluginData.getMessageUtil().sendErrorMessage(event.getPlayer(), "You can't edit terrain where a minigame is active.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        List<Location> locations = new ArrayList<>();

        if (!hiddenPlayers.isEmpty() && seeker != null) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.contains(event.getPlayer())) {
                for (int x = getWarp().getBlockX() - radius; x <= getWarp().getBlockX() + radius; x++) {
                    for (int y = getWarp().getBlockY() - radius; y <= getWarp().getBlockY() + radius; y++) {
                        for (int z = getWarp().getBlockZ() - radius; z <= getWarp().getBlockZ() + radius; z++) {
                            locations.add(new Location(event.getPlayer().getWorld(), x, y, z));
                        }
                    }
                }

                if (locations.contains(event.getBlock().getLocation())) {
                    if (!getPlayers().contains(event.getPlayer().getUniqueId())) {
                        PluginData.getMessageUtil().sendErrorMessage(event.getPlayer(), "You can't edit terrain where a minigame is active.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (!hiddenPlayers.isEmpty() && seeker != null) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.contains(event.getPlayer())) {
                if (event.getMessage().startsWith("/up") || event.getMessage().startsWith("//up")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public boolean joinAllowed() {
        return isAnnounced() && !(hiding || seeking);
    }

    @Override
    public String getGameChatTag(Player player) {
        if(seeker.contains(player)) {
            return ChatColor.GOLD + "<Seeker ";
        }
        else {
            return super.getGameChatTag(player);
        }
    }

    /*

    public boolean teleportToManager(Player manager, OfflinePlayer player) {
        if (PlayerUtil.getOnlinePlayer(player) != null) {
            if (getOnlinePlayers().contains((Player) player)) {
                forceTeleport((Player) player, manager.getLocation());
                return true;
            } else {
                sendPlayerNotInGame(manager);
                return false;
            }
        } else {
            sendPlayerNotOnline(manager);
            return false;
        }
    }

    public void teleportToWarp(Player player) {
        player.teleport(getWarp(), TeleportCause_FORCE);
    }

     */

    private void sendStartHideMessage() {
        for(Player player : getOnlinePlayers()) {
                TitleUtil.showTitle(player, ChatColor.YELLOW+" RUN!!!"," ");
        }
    }

    public void sendRadiusMessage(){
        for(Player player : getOnlinePlayers()){
            PluginData.getMessageUtil().sendInfoMessage(player, "The radius is "+ String.valueOf(radius));
        }
    }

    private void sendStartSeekingMessage() {
        for(Player player : getOnlinePlayers()) {
            if(!seeker.contains(player)) {
                TitleUtil.showTitle(player, ChatColor.BLUE+" RUN!!!","The hunters are hunting.");
            }else{
                TitleUtil.showTitle(player, ChatColor.YELLOW+" HUNT!!!"," Try to find the other players.");
            }
        }
    }

    private void sendStopSeekingMessage() {
        if(hiddenPlayers.isEmpty()) {
            for(OfflinePlayer OP:seeker){
                TitleUtil.showTitle((Player) OP, ChatColor.GOLD+"YOU WON", "You found all players.");
            }
        }
        else {
            TitleUtil.showTitle((Player) seeker, ChatColor.BLUE+"GAME OVER", "You found not all players.");
        }
        for(Player player : getOnlinePlayers()) {
            if(!seeker.contains(player)) {
                if(hiddenPlayers.isEmpty()) {
                    TitleUtil.showTitle(player, ChatColor.BLUE+"GAME OVER", "All players were hunted down.");
                }
                else if (isHidden(player)) {
                    TitleUtil.showTitle(player, ChatColor.GOLD+"YOU WON", "The Hunters didn´t find you.");
                }
                else {
                    TitleUtil.showTitle(player, ChatColor.BLUE+"GAME OVER", "You were hunted, but not all players");
                }
            }
        }
    }

    private void sendSeekerLeavingMessage(Player player) {
        GameChatUtil.sendAllInfoMessage(player, this, "The hunters left.");
    }

    private void sendPlayerFoundMessage(Player hidden) {
        PluginData.getMessageUtil().sendInfoMessage(hidden, "You were found.");
        hidden.playEffect(hidden.getLocation(),Effect.BLAZE_SHOOT,0);
        for(OfflinePlayer OP:seeker){
            PluginData.getMessageUtil().sendInfoMessage((CommandSender) OP, "You found "+ hidden.getName() + ".");
        }
    }

    /*
    public void sendHiddenList(Player player){
        List<String> hiddenPlayers = new ArrayList<>();
        for(Player hidden : this.hiddenPlayers){
            hiddenPlayers.add(hidden.getName());
        }
        PluginData.getMessageUtil().sendInfoMessage(player, "These are the still hidden players:");
        PluginData.getMessageUtil().sendInfoMessage(player, hiddenPlayers.toString());
    }

    public void setGlow(Player manager, boolean allowed){
        for(Player player: getOnlinePlayers()){
            player.setGlowing(allowed);
        }
    }

     */
    public void setSeekTime(int seekTime) {
        this.seekTime = seekTime;
    }

    public void setRadius(int radius){ this.radius = radius; }

    public void setHideTime(int hideTime) {
        this.hideTime = hideTime;
    }

    public boolean isSeeking() {
        return seeking;
    }

    public boolean isHiding() {
        return hiding;
    }

    /*
    public OfflinePlayer getSeeker() {
        return seeker;
    }

    public List<Player> getHiddenPlayers() {
        return hiddenPlayers;
    }

     */

    private void sendPlayerNotInGame(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You can´t teleport this player, he is not part of your game.");
    }

    private void sendPlayerNotOnline(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You can´t teleport this player, he is not online.");
    }
}

