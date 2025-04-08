package com.mcmiddleearth.minigames.spigot;

import com.mcmiddleearth.minigames.common.Channels;
import com.mcmiddleearth.minigames.spigot.listener.QuizListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jubo
 */
public class MiniGamesPaper extends JavaPlugin implements Listener {
    static MiniGamesPaper instance;
    private final QuizListener quizListener = new QuizListener();

    @Override
    public void onEnable(){
        if(MiniGamesPaper.instance == null)
            MiniGamesPaper.instance = this;
        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.QUIZ.getNamespace());
        getServer().getMessenger().registerIncomingPluginChannel(this, Channels.QUIZ.getNamespace(), quizListener);
        getServer().getPluginManager().registerEvents(quizListener, this);
        Logger.getLogger("MiniGames").log(Level.INFO, "Loaded paper plugin.");
    }

    @Override
    public void onDisable(){

    }

    static public MiniGamesPaper getInstance(){return instance;}
}
