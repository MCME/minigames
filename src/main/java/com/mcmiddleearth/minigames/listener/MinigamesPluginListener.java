package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.util.Channel;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class MinigamesPluginListener implements Listener {

    @EventHandler
    public void onMessage(PluginMessageEvent event){
        if(!event.getTag().equals(Channel.MAIN)){
            return;
        }
        event.setCancelled(true);
    }
}
