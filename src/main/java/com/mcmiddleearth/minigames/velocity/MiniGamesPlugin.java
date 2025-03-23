package com.mcmiddleearth.minigames.velocity;

import com.google.inject.Inject;
import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.listener.ConfirmationListener;
import com.mcmiddleearth.minigames.listener.PlayerListener;
import com.mcmiddleearth.minigames.listener.quizListener.askQuestion;
import com.mcmiddleearth.minigames.listener.quizListener.editQuestion;
import com.mcmiddleearth.minigames.listener.quizListener.submitQuestion;
import com.mcmiddleearth.minigames.common.Channels;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.velocity.scoreboard.generics.AbstractGameScoreboard;
import com.mcmiddleearth.minigames.velocity.scoreboard.generics.ScoreboardObjective;
import com.mcmiddleearth.minigames.velocity.scoreboard.generics.ScoreboardScore;
import com.mcmiddleearth.minigames.velocity.util.GameManager;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "mini_games_plugin", name = "Mini Games Plugin", version = "3.0",
        url = "https://github.com/MCME", description = "A plugin to run mini games on the MCME server.", authors = {"Nic", "Jubo", "Eriol"})
public final class MiniGamesPlugin {

    private static MiniGamesPlugin instance;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final GameManager gameManager = new GameManager();

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
        EventManager eventManager = server.getEventManager();
        CommandManager commandManager = server.getCommandManager();
        ChannelRegistrar channelRegistrar = server.getChannelRegistrar();

        PluginData.load();

        eventManager.register(this, new MiniGamesPluginListener());
        eventManager.register(this, new PlayerListener());
        eventManager.register(this, new ConfirmationListener());
        eventManager.register(this, new submitQuestion());
        eventManager.register(this, new editQuestion());
        eventManager.register(this, new askQuestion());

        channelRegistrar.register(Channels.MAIN);
        channelRegistrar.register(AbstractGameScoreboard.IDENTIFIER);
        channelRegistrar.register(ScoreboardObjective.IDENTIFIER);
        channelRegistrar.register(ScoreboardScore.IDENTIFIER);
        channelRegistrar.register(GameManager.GAMEMANAGER);

//        commandManager.register(new MinigamesPluginCommand(new GcCommandHandler("gc"), "gc"));
//        commandManager.register(new MinigamesPluginCommand(new GameCommandHandler("game"),"game"));
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