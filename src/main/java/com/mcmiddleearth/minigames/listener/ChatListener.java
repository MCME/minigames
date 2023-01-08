package com.mcmiddleearth.minigames.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    //For the Conversations in geoguessr and quizes
    @EventHandler
    public void conversationHandler(ChatEvent event){
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        if(!message.startsWith("/")){

        }
    }
}
