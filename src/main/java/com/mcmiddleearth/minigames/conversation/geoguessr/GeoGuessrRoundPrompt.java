package com.mcmiddleearth.minigames.conversation.geoguessr;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

/**
 *
 * @author Jubo
 */

public class GeoGuessrRoundPrompt extends StringPrompt {

    @Override
    public String getPromptText(ConversationContext cc){
        cc.setSessionData("input",true);
        return ChatColor.DARK_GREEN+"[Hint]Type your answer in chat.";
    }

    @Override
    public Prompt acceptInput(ConversationContext cc, String string){
        cc.setSessionData("answer",string);
        return END_OF_CONVERSATION;
    }
}
