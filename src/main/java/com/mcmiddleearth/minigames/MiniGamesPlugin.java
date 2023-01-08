package com.mcmiddleearth.minigames;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.command.MinigamesPluginCommand;
import com.mcmiddleearth.minigames.command.handler.GameCommandHandler;
import com.mcmiddleearth.minigames.command.handler.GcCommandHandler;
import com.mcmiddleearth.minigames.listener.ChatListener;
import com.mcmiddleearth.minigames.listener.MinigamesPluginListener;
import com.mcmiddleearth.minigames.util.PluginData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 *
 * @author Jubo
 */
public final class MiniGamesPlugin extends Plugin {

    private static MiniGamesPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        PluginData.load();

        ProxyServer.getInstance().getPluginManager().registerListener(this,new MinigamesPluginListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this,new ChatListener());

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