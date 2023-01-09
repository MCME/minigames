package com.mcmiddleearth.minigames.quiz;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.minigames.util.PluginData;

/**
 * @author Jubo
 */
public class QuizShowCategories {

    public static void execute(McmeCommandSender sender){
        for(String line: PluginData.getQuestionCategoriesFile()){
            //PluginData.getMessageUtil().sendInfoMessage(sender,"Test");
            PluginData.getMessageUtil().sendInfoMessage(sender,line);
        }
    }
}