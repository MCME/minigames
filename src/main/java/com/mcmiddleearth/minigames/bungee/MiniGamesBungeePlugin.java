package com.mcmiddleearth.minigames.bungee;

import com.mcmiddleearth.base.core.command.McmeCommandSender;
import com.mcmiddleearth.minigames.bungee.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.bungee.command.MinigamesPluginCommand;
import com.mcmiddleearth.minigames.bungee.listener.ConfirmationListener;
import com.mcmiddleearth.minigames.bungee.listener.MinigamesPluginListener;
import com.mcmiddleearth.minigames.bungee.listener.PlayerListener;
import com.mcmiddleearth.minigames.bungee.listener.quizListener.askQuestion;
import com.mcmiddleearth.minigames.bungee.listener.quizListener.editQuestion;
import com.mcmiddleearth.minigames.bungee.listener.quizListener.submitQuestion;
import com.mcmiddleearth.minigames.core.command.handler.GameCommandHandler;
import com.mcmiddleearth.minigames.core.command.handler.GcCommandHandler;
import com.mcmiddleearth.minigames.core.util.PluginData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author Jubo
 */
public final class MiniGamesBungeePlugin extends Plugin {

    private static MiniGamesBungeePlugin instance;

    /*
    TODO:
        werewolf:
            rework the voting system (maybe clickable?)
        quiz:
            i did /newgame loadquestions a 1 a couple of times after finishing the previously done 15 questions and each time, everyones scores would reset back to 0
     */

    @Override
    public void onEnable() {
        instance = this;

        PluginData.load();

        ProxyServer.getInstance().getPluginManager().registerListener(this,new MinigamesPluginListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this,new PlayerListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this,new ConfirmationListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this,new submitQuestion());
        ProxyServer.getInstance().getPluginManager().registerListener(this,new editQuestion());
        ProxyServer.getInstance().getPluginManager().registerListener(this,new askQuestion());

        ProxyServer.getInstance().getPluginManager().registerCommand(this,new MinigamesPluginCommand(new GcCommandHandler("gc"),"gc"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this,new MinigamesPluginCommand(new GameCommandHandler("game"),"game"));
    }

    @Override
    public void onDisable() {
        PluginData.clearGames();
    }

    public static McmeCommandSender wrapCommandSender(CommandSender sender){
        return MinigameCommandSender.getOrCreateMcmePlayer((ProxiedPlayer) sender);
    }

    public static MiniGamesBungeePlugin getInstance(){return instance;}
}