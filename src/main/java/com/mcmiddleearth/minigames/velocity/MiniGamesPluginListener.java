package com.mcmiddleearth.minigames.velocity;

import com.mcmiddleearth.minigames.common.Channels;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;


public class MiniGamesPluginListener implements EventHandler<PluginMessageEvent> {

    @Subscribe
    @Override
    public void execute(PluginMessageEvent event) {
        if(!Channels.MAIN.equals(event.getIdentifier())){
            return;
        }
        event.setResult(PluginMessageEvent.ForwardResult.handled());
    }
}
