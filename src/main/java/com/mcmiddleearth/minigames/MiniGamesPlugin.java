package com.mcmiddleearth.minigames;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.listener.MiniGamesPluginListener;
import com.mcmiddleearth.minigames.util.Channel;
import com.mcmiddleearth.minigames.util.PluginData;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;

import java.nio.file.Path;
import java.util.logging.Logger;

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
//        server.getEventManager().register(this, new PlayerListener());
//        server.getEventManager().register(this, new ConfirmationListener());
//        server.getEventManager().register(this, new submitQuestion());
//        server.getEventManager().register(this, new editQuestion());
//        server.getEventManager().register(this, new askQuestion());
        server.getChannelRegistrar().register(Channel.MAIN);

//        server.getCommandManager().register(new MinigamesPluginCommand(new GcCommandHandler("gc"), "gc"));
    }
//    @Override
//    public void onEnable() {
//
//
//        ProxyServer.getInstance().getPluginManager().registerCommand(this,new MinigamesPluginCommand(new GcCommandHandler("gc"),"gc"));
//        ProxyServer.getInstance().getPluginManager().registerCommand(this,new MinigamesPluginCommand(new GameCommandHandler("game"),"game"));
//    }
//
//    @Override
//    public void onDisable() {
//        PluginData.clearGames();
//    }

    @Subscribe
    public void onPluginMessageFromPlugin(PluginMessageEvent event) {
        logger.info("got event");
        // Check if the identifier matches first, no matter the source.
        if (!Channel.MAIN.equals(event.getIdentifier())||!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        // mark PluginMessage as handled, indicating that the contents
        // should not be forwarding to their original destination.
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        // handle packet data
        logger.info("got data");
    }

    public Logger getLogger(){return logger;}
    public Path getDataDirectory(){return dataDirectory;}
    public static McmeCommandSender wrapCommandSender(Player sender){
        return MinigameCommandSender.getOrCreateMcmePlayer(sender);
    }

    public static MiniGamesPlugin getInstance(){return instance;}
    public ProxyServer getProxyServer(){return server;}
}