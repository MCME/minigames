package com.mcmiddleearth.minigames.spigot;

import com.mcmiddleearth.minigames.spigot.listener.MinigameMessageListener;
import com.mcmiddleearth.minigames.util.Channel;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Jubo
 */
public class MiniGamesSpigot extends JavaPlugin {

    @Override
    public void onEnable(){
        getServer().getMessenger().registerIncomingPluginChannel(this, Channel.MAIN,new MinigameMessageListener());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this,Channel.MAIN);
    }

    @Override
    public void onDisable(){

    }
}
