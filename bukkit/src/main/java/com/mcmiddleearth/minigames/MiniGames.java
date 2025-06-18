package com.mcmiddleearth.minigames;

import com.mcmiddleearth.minigames.listener.QuizListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jubo
 */
public class MiniGames extends JavaPlugin implements Listener {
    static MiniGames instance;
    private final QuizListener quizListener = new QuizListener();

    @Override
    public void onEnable(){
        if(MiniGames.instance == null)
            MiniGames.instance = this;
        getServer().getMessenger().registerOutgoingPluginChannel(this, SpigotChannels.QUIZ);
        getServer().getMessenger().registerIncomingPluginChannel(this, SpigotChannels.QUIZ, quizListener);
        getServer().getPluginManager().registerEvents(quizListener, this);
        Logger.getLogger("MiniGames").log(Level.INFO, "Loaded paper plugin.");
    }

    @Override
    public void onDisable(){

    }

    static public MiniGames getInstance(){return instance;}
}
