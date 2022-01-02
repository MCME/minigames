package com.mcmiddleearth.minigames.conversation.geoguessr;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

/**
 *
 * @author Jubo
 */

public class GeoGuessrRoundPrefix implements ConversationPrefix {

    @Override
    public String getPrefix(ConversationContext cc){
        if((Boolean)cc.getSessionData("input")){
            cc.setSessionData("input",false);
            return ChatColor.BLUE+"[Your Answer]";
        }
        return ChatColor.AQUA+"";
    }
}
