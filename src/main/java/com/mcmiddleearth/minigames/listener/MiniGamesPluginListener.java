package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.util.Channel;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;


public class MiniGamesPluginListener implements EventHandler<PluginMessageEvent> {

    @Subscribe
    @Override
    public void execute(PluginMessageEvent event) {
        if(!Channel.MAIN.equals(event.getIdentifier())){
            return;
        }
        event.setResult(PluginMessageEvent.ForwardResult.handled());
    }
}
