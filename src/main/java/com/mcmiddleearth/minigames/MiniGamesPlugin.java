package com.mcmiddleearth.minigames;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.command.MinigamesPluginCommand;
import com.mcmiddleearth.minigames.command.handler.GameCommandHandler;
import com.mcmiddleearth.minigames.command.handler.GcCommandHandler;
import com.mcmiddleearth.minigames.listener.ConfirmationListener;
import com.mcmiddleearth.minigames.listener.MinigamesPluginListener;
import com.mcmiddleearth.minigames.listener.PlayerListener;
import com.mcmiddleearth.minigames.listener.quizListener.askQuestion;
import com.mcmiddleearth.minigames.listener.quizListener.editQuestion;
import com.mcmiddleearth.minigames.listener.quizListener.submitQuestion;
import com.mcmiddleearth.minigames.util.PluginData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author Jubo
 */
public final class MiniGamesPlugin extends Plugin {

    private static MiniGamesPlugin instance;

    /*
    TODO:
        werewolf:
            rework the voting system (maybe clickable?)
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

    public static MiniGamesPlugin getInstance(){return instance;}
}