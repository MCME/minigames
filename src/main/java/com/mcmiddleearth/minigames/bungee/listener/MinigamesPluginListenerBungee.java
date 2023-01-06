package com.mcmiddleearth.minigames.bungee.listener;

import com.mcmiddleearth.minigames.bungee.Channel;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Jubo
 */
public class MinigamesPluginListenerBungee implements Listener{

    public MinigamesPluginListenerBungee(){}

    @EventHandler
    public void onMessage(PluginMessageEvent event){
        if(!event.getTag().equals(Channel.MAIN)){
            return;
        }
        event.setCancelled(true);
    }

}
