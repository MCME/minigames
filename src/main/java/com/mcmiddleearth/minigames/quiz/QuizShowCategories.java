package com.mcmiddleearth.minigames.quiz;

import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.util.PluginData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * @author Jubo
 */
public class QuizShowCategories {

    public static void execute(McmeCommandSender sender){
        for(String line: PluginData.getQuestionCategoriesFile()){
            sender.sendMessage(new ComponentBuilder(line).color(ChatColor.AQUA).create());
        }
    }
}