package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.scoreboard.WerewolfGameScoreboard;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class WerewolfGame extends AbstractGame implements Listener {

    private BossBar bar;

    private Player votee;
    public WerewolfGame(Player manager, String name){
        super(manager,name,GameType.WEREWOLF,new WerewolfGameScoreboard());

        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setTeleportAllowed(false);
        setFlightAllowed(false);
        setGm2Forced(true);
        setCollision(true);
        announceGame();

        BossBar bar = Bukkit.createBossBar(ChatColor.YELLOW+"Werewolf", BarColor.WHITE, BarStyle.SOLID);
        bar.setProgress(1.0);
        bar.setVisible(true);
        this.bar = bar;
    }

    public void start(){
        ((WerewolfGameScoreboard)this.getBoard()).start();
    }

    public void eliminate(Player player){
        if(player == votee){
            ((WerewolfGameScoreboard)getBoard()).reset();
        }
        removePlayer(player);
        player.getInventory().setHelmet(new ItemStack(Material.SKELETON_SKULL));
        addSpectator(player);
    }

    public void putUpVote(Player player){
        votee=player;

    }

    public void suggest(Player player){
        ((WerewolfGameScoreboard)getBoard()).suggest(player.getName());
    }

    public void vote(boolean bool){

    }



    @Override
    public void addPlayer(Player player){
        bar.addPlayer(player);
        if((player == getManager().getPlayer())){
            getBoard().incrementPlayer();
            player.setScoreboard((this.getBoard()).getScoreboard());
        }else{
            super.addPlayer(player);
            player.setSneaking(true);
            forceTeleport(player,getWarp());
            ((WerewolfGameScoreboard)getBoard()).addPlayer(player.getName());
        }
    }

    @Override
    public void playerMove(PlayerMoveEvent event){
        super.playerMove(event);
        event.getPlayer().stopSound(Sound.BLOCK_STONE_STEP);
    }

    @Override
    public boolean joinAllowed() {
        return isAnnounced();
    }

    //public void teleportToWarp(Player player){player.teleport(getWarp(),TeleportCause_FORCE);}

    @Override
    public int allowedRadius(Player player){
        return 75;
    }
}
