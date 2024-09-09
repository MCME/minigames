package com.mcmiddleearth.minigames.core.util;

import com.mcmiddleearth.base.core.message.MessageColor;

/**
 * @author Jubo, Eriol_Eandur
 */
public enum ChatRanks {

    GAME_MASTER     (MessageColor.DARK_RED, "<Game Master> "),
    MANAGER         (MessageColor.DARK_AQUA, "<Manager> "),
    RACER           (MessageColor.BLUE, "<Racer> "),
    SEEKER          (MessageColor.GOLD, "<Seeker> "),
    HUNTER          (MessageColor.GOLD, "Hunter> "),
    Participant     (MessageColor.BLUE, "<Participant> ");

    private final String chatPrefix;
    private final MessageColor color;

    ChatRanks(MessageColor color, String chatPrefix){
        this.chatPrefix = chatPrefix;
        this.color = color;
    }

    public String getChatPrefix(){
        return chatPrefix;
    }

    public MessageColor getColor() {
        return color;
    }
}
