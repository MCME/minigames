package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.scoreboard.WerewolfGameScoreboard;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jubo
 */
public class WerewolfGame extends AbstractGame implements Listener {

    private BossBar bar;

    private Player votee;
    private static Player manager;

    private  List<Player> eliminated = new ArrayList<>();
    private List<Player> voted = new ArrayList<>();

    public WerewolfGame(Player manager, String name){
        super(manager,name,GameType.WEREWOLF,new WerewolfGameScoreboard());
        this.manager = manager;
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
        sendGameStartMessage();
    }

    public void eliminate(Player player){
        if(player == votee){
            ((WerewolfGameScoreboard)getBoard()).reset(player.getName());
            voted.clear();
        }
        removePlayer(player);
        player.getInventory().setHelmet(new ItemStack(Material.SKELETON_SKULL));
        addSpectator(player);
        player.setSilent(false);
        eliminated.add(player);
    }

    public void putUpVote(Player player){
        votee=player;
        ((WerewolfGameScoreboard)getBoard()).putUpForVote(player.getName());
    }

    public void suggest(CommandSender cs, Player player){
        if(manager != player) {
            if (eliminated.contains(player)) {
                sendAlreadyEliminatedMessage(cs);
            } else {
                if (!voted.contains(((Player) cs))) {
                    ((WerewolfGameScoreboard) getBoard()).suggest(player.getName());
                    voted.add((Player) cs);
                } else {
                    sendAlreadyVotedMessage(cs);
                }
            }
        }else{
            sendVoteManager(cs);
        }
    }

    public void vote(CommandSender cs,boolean bool){
        if(!voted.contains(((Player)cs))) {
            ((WerewolfGameScoreboard) getBoard()).vote(bool);
            voted.add((Player) cs);
        } else{
            sendAlreadyVotedMessage(cs);
        }
    }



    @Override
    public void addPlayer(Player player){
        bar.addPlayer(player);

        super.addPlayer(player);
        player.setSneaking(true);
        forceTeleport(player,getWarp());
        player.setSilent(true);
        ((WerewolfGameScoreboard)getBoard()).addPlayer(player.getName());



        /*
        if((player == getManager().getPlayer())){
            getBoard().incrementPlayer();
            player.setScoreboard((this.getBoard()).getScoreboard());
        }else{
            super.addPlayer(player);
            player.setSneaking(true);
            forceTeleport(player,getWarp());
            ((WerewolfGameScoreboard)getBoard()).addPlayer(player.getName());
        }

         */


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

    @Override
    public void end(Player sender){
        super.end(sender);
        for(Player player: getOnlinePlayers()){
            bar.removePlayer(player);
            player.setSilent(false);
        }
    }

    private void sendGameStartMessage(){
        PluginData.getMessageUtil().sendBroadcastMessage("The game was started.");
    }

    private void sendAlreadyVotedMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"You already voted on this one.");
    }

    private void sendVoteManager(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"You can´t suggest the manager.");
    }

    private void sendAlreadyEliminatedMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"This player was already eliminated.");
    }
}
