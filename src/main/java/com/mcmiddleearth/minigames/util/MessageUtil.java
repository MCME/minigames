package com.mcmiddleearth.minigames.util;

import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

/**
 *
 * @author Jubo
 */
public class MessageUtil {

    private final String PREFIX = "[MCME-Minigames] ";


//    public void sendErrorMessage(Player sender, String message){
//        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.ERROR).create());
//    }
//
//    public void sendInfoMessage(Player sender, String message){
//        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.INFO).create());
//    }
//
//    public void sendBroadcastMessage(String message){
//        for(Player player: ProxyServer.getAllPlayers()){
//            player.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.INFO).create());
//        }
//    }
//
//    public void sendErrorMessage(McmeCommandSender sender, String message){
//        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.ERROR).create());
//    }
//
//    public void sendInfoMessage(McmeCommandSender sender, String message){
//        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.INFO).create());
//    }
//
//    public void sendClickableInfoMessage(Player sender, String message, String clickable){
//        TextComponent text = new TextComponent(message);
//        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,clickable));
//        sender.sendMessage(text);
//    }

    public String getPREFIX(){return PREFIX;}
}
