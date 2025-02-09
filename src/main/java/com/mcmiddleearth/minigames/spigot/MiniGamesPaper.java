package com.mcmiddleearth.minigames.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.spigot.listener.MiniGameMessageListener;
import com.mcmiddleearth.minigames.util.Channel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jubo
 */
public class MiniGamesPaper extends JavaPlugin implements Listener {
    @Override
    public void onEnable(){
        getServer().getMessenger().registerIncomingPluginChannel(this, Channel.MAIN_PAPER,new MiniGameMessageListener());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this,Channel.MAIN_PAPER);
        Logger.getLogger("MiniGames").log(Level.INFO, "Loaded paper plugin.");
    }

    @Override
    public void onDisable(){

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Logger.getLogger("MiniGames").log(Level.INFO, "Join event done.");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(e.getPlayer().getName());
        e.getPlayer().sendPluginMessage(this, Channel.MAIN_PAPER,
                out.toByteArray());
    }
}
