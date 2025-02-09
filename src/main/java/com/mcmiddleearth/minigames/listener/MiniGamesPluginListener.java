package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.util.Channel;
import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import org.slf4j.event.Level;

import java.util.Arrays;


public class MiniGamesPluginListener implements EventHandler<PluginMessageEvent> {

    @Subscribe
    @Override
    public void execute(PluginMessageEvent event) {
        MiniGamesPlugin.getInstance().getLogger().info("got an event");
        if(!Channel.MAIN.equals(event.getIdentifier())){
            return;
        }
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        MiniGamesPlugin.getInstance().getLogger().info("message received");
    }
}
