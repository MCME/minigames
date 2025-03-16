package com.mcmiddleearth.minigames;

import com.google.inject.Inject;
import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.listener.ConfirmationListener;
import com.mcmiddleearth.minigames.listener.MiniGamesPluginListener;
import com.mcmiddleearth.minigames.listener.PlayerListener;
import com.mcmiddleearth.minigames.listener.quizListener.askQuestion;
import com.mcmiddleearth.minigames.listener.quizListener.editQuestion;
import com.mcmiddleearth.minigames.listener.quizListener.submitQuestion;
import com.mcmiddleearth.minigames.util.Channel;
import com.mcmiddleearth.minigames.util.PluginData;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "mini_games_plugin", name = "Mini Games Plugin", version = "3.0",
        url = "https://github.com/MCME", description = "A plugin to run mini games on the MCME server.", authors = {"NicovicTheSixth", "Jubo"})
public final class MiniGamesPlugin {

    private static MiniGamesPlugin instance;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    /*
    TODO:
        werewolf:
            rework the voting system (maybe clickable?)
        quiz:
            i did /newgame loadquestions a 1 a couple of times after finishing the previously done 15 questions and each time, everyones scores would reset back to 0
     */
    @Inject
    public MiniGamesPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        logger.info("Hello there, it's a test plugin I made!");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Do some operation demanding access to the Velocity API here.
        // For instance, we could register an event:
        instance = this;

        PluginData.load();

        server.getEventManager().register(this, new MiniGamesPluginListener());
        server.getEventManager().register(this, new PlayerListener());
        server.getEventManager().register(this, new ConfirmationListener());
        server.getEventManager().register(this, new submitQuestion());
        server.getEventManager().register(this, new editQuestion());
        server.getEventManager().register(this, new askQuestion());
        server.getChannelRegistrar().register(Channel.MAIN);

//        server.getCommandManager().register(new MinigamesPluginCommand(new GcCommandHandler("gc"), "gc"));
//        server.getCommandManager().register(new MinigamesPluginCommand(new GameCommandHandler("game"),"game"));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        PluginData.clearGames();
    }

    public Logger getLogger(){return logger;}
    public Path getDataDirectory(){return dataDirectory;}
    public static McmeCommandSender wrapCommandSender(Player sender){
        return MinigameCommandSender.getOrCreateMcmePlayer(sender);
    }

    public static MiniGamesPlugin getInstance(){return instance;}
    public ProxyServer getProxyServer(){return server;}
}