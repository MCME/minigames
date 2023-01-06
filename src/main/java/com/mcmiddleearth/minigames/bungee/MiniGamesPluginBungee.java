package com.mcmiddleearth.minigames.bungee;

import com.mcmiddleearth.minigames.bungee.listener.MinigamesPluginListenerBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 *
 * @author Jubo
 */
public class MiniGamesPluginBungee extends Plugin{

    private static MiniGamesPluginBungee instance;

    @Override
    public void onEnable(){
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new HelloWorld("hello"));
        ProxyServer.getInstance().getPluginManager().registerListener(this,new MinigamesPluginListenerBungee());
    }


    @Override
    public void onDisable(){

    }

    public static MiniGamesPluginBungee getInstance(){
        return instance;
    }
}
