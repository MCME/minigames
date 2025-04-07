package com.mcmiddleearth.minigames.spigot.util;

import net.md_5.bungee.api.ChatColor;

/**
 * @author Jubo
 */
public enum ChatRanks {

    GAME_MASTER     (ChatColor.DARK_RED+"<Game Master> "),
    MANAGER         (ChatColor.DARK_AQUA+"<Manager> "),
    RACER           (ChatColor.BLUE+"<Racer> "),
    SEEKER          (ChatColor.GOLD+"<Seeker> "),
    HUNTER          (ChatColor.GOLD+"Hunter> "),
    Participant     (ChatColor.BLUE+"<Participant> ");

    private final String chatPrefix;

    ChatRanks(String chatPrefix){
        this.chatPrefix = chatPrefix;
    }

    public String getChatPrefix(){
        return chatPrefix;
    }
}
