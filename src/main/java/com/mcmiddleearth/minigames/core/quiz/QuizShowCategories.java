package com.mcmiddleearth.minigames.core.quiz;

import com.mcmiddleearth.base.core.command.McmeCommandSender;
import com.mcmiddleearth.minigames.core.MiniGames;
import com.mcmiddleearth.minigames.core.util.PluginData;
import com.mcmiddleearth.minigames.core.util.Style;

/**
 * @author Jubo, Eriol_Eandur
 */
public class QuizShowCategories {

    public static void execute(McmeCommandSender sender){
        for(String line: PluginData.getQuestionCategoriesFile()){
            sender.sendMessage(MiniGames.getPlugin().createMessage().add(line, Style.INFO));
        }
    }
}