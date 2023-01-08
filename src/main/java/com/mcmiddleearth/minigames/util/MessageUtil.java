package com.mcmiddleearth.minigames.util;

import com.mcmiddleearth.command.McmeCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author Jubo
 */
public class MessageUtil {

    private final String PREFIX = "[MCME-Minigames] ";


    public void sendErrorMessage(CommandSender sender, String message){
        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.ERROR).create());
    }

    public void sendInfoMessage(CommandSender sender, String message){
        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.INFO).create());
    }

    public void sendBroadcastMessage(String message){
        for(ProxiedPlayer player: ProxyServer.getInstance().getPlayers()){
            player.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.INFO).create());
        }
    }

    public void sendErrorMessage(McmeCommandSender sender, String message){
        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.ERROR).create());
    }

    public void sendInfoMessage(McmeCommandSender sender, String message){
        sender.sendMessage(new ComponentBuilder(PREFIX+message).color(Style.INFO).create());
    }

    public void sendClickableInfoMessage(CommandSender sender, String message, String clickable){
        TextComponent text = new TextComponent(message);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,clickable));
        sender.sendMessage(text);
    }

    public String getPREFIX(){return PREFIX;}
}
