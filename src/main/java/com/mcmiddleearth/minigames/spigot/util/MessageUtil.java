package com.mcmiddleearth.minigames.spigot.util;

import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;

/**
 *
 * @author Jubo
 */
public class MessageUtil {

    static private final String PREFIX = "[MCME-Minigames] ";


    static public void sendErrorMessage(Player sender, String message){
        sender.sendMessage(Component.text(PREFIX+message).color(Style.ERROR));
    }

    static public void sendInfoMessage(Player sender, String message){
        sender.sendMessage(Component.text(PREFIX+message).color(Style.INFO));
    }

    static public void sendBroadcastMessage(String message){
        for(Player player: MiniGamesPlugin.getInstance().getProxyServer().getAllPlayers()){
            player.sendMessage(Component.text(PREFIX+message));
        }
    }

    static public void sendErrorMessage(McmeCommandSender sender, String message){
        //sender.sendMessage(Component.text(PREFIX+message).color(Style.ERROR));
    }

    static public void sendInfoMessage(McmeCommandSender sender, String message){
        //sender.sendMessage(Component.text(PREFIX+message).color(Style.INFO));
    }

    static public void sendClickableInfoMessage(Player sender, String message, String clickable){
        Component text = Component.text(message);
        text.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,clickable));
        sender.sendMessage(text);
    }

    static public String getPREFIX(){return PREFIX;}
}
