package com.mcmiddleearth.minigames.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.spigot.listener.MiniGameMessageListener;
import com.mcmiddleearth.minigames.common.Channels;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.MAIN_PAPER,new MiniGameMessageListener());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.MAIN_PAPER);
        Logger.getLogger("MiniGames").log(Level.INFO, "Loaded paper plugin.");
    }

    @Override
    public void onDisable(){

    }

    @EventHandler
    public void onJoin(AsyncChatEvent e) {
        Player player = e.getPlayer();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getName());
        player.sendPluginMessage(this, Channels.MAIN_PAPER, out.toByteArray());
    }
}
