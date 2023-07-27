package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.scoreboard.CatchGameScoreboard;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CatchGame extends AbstractGame implements Listener {

    public OfflinePlayer catcher;

    int defaultRadius = 35;
    int radius = defaultRadius;
    int defaultTime = 300;
    int time = defaultTime;
    int defaultCountdown = 25;
    int countdown = defaultCountdown;

    public CatchGame(Player manager, String name){
        super(manager,name,GameType.CATCH,new CatchGameScoreboard());

        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setTeleportAllowed(false);
        setFlightAllowed(false);
        setGm2Forced(true);
        setCollision(true);
        announceGame();
    }

    public void setCatcher(OfflinePlayer catcher){this.catcher = catcher;}

    public void catching(int radius,int time,int countdown){
        if(radius > defaultRadius){this.radius = radius;}
        if(time > defaultTime){this.time = time;}
        if(countdown > defaultCountdown){this.countdown = countdown;}

    }

    @Override
    public void addPlayer(Player player){
        super.addPlayer(player);
        forceTeleport(player,getWarp());
    }

    @Override
    public void removePlayer(OfflinePlayer player){
        super.removePlayer(player);
        if(catcher != null && PlayerUtil.isSame(player,catcher)){
            setRandomCatcher();
        }
    }

    private void setRandomCatcher(){
        //chooses a random Player as catcher from the players still in game
    }

    @Override
    public void playerLeaveServer(PlayerQuitEvent event){
        super.playerLeaveServer(event);
        Player player = event.getPlayer();
        if(PlayerUtil.isSame(player,catcher)){
            sendCatcherLeavingMessage(player);
            setRandomCatcher();
        }
    }


    private void sendCatcherLeavingMessage(Player player) {
        GameChatUtil.sendAllInfoMessage(player, this, "The catcher left.");
    }

    @Override
    public void playerMove(PlayerMoveEvent event){
        super.playerMove(event);
    }

    @Override
    public void playerInteract(PlayerInteractEntityEvent event){
        Player catcher = event.getPlayer();
        if(event.getRightClicked() instanceof Player){
            Player player = (Player) event.getRightClicked();
            setCatcher(player);
        }
    }
}
