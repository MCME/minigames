package com.mcmiddleearth.minigames.util;

import com.mcmiddleearth.minigames.Style;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class MessageUtil {
    static private final String PREFIX = "[MCME-Minigames] ";

    static public void sendErrorMessage(Audience sender, String message){
        sender.sendMessage(Component.text(PREFIX+message).color(Style.ERROR));
    }

    static public void sendInfoMessage(Audience sender, String message){
        sender.sendMessage(Component.text(PREFIX+message).color(Style.INFO));
    }
}
