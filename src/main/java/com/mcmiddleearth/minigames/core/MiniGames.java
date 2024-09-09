package com.mcmiddleearth.minigames.core;

import com.mcmiddleearth.base.core.logger.McmeLogger;
import com.mcmiddleearth.base.core.message.Message;
import com.mcmiddleearth.base.core.message.MessageColor;
import com.mcmiddleearth.base.core.message.MessageDecoration;
import com.mcmiddleearth.base.core.plugin.McmeProxyPlugin;
import com.mcmiddleearth.base.core.server.McmeProxy;

public class MiniGames {

    private static McmeProxyPlugin plugin;
    private static McmeLogger logger;

    public static void enable(McmeProxyPlugin plugin) {
        MiniGames.plugin = plugin;
    }

    public static void disable() {

    }

    public static McmeProxyPlugin getPlugin() {
        return plugin;
    }

    public static McmeProxy getProxy() {
        return plugin.getMcmeProxy();
    }

    public static Message infoMessage() {
        return getPlugin().createInfoMessage();
    }

    public static  Message errorMessage() {
        return getPlugin().createErrorMessage();
    }
    public static Message infoMessage(String message) {
        return getPlugin().createInfoMessage().add(message);
    }

    public static  Message errorMessage(String message) {
        return getPlugin().createErrorMessage().add(message);
    }

    public static  Message message(String message, MessageColor color) {
        return getPlugin().createMessage().add(message, color);
    }
    public static  Message message(String message, MessageColor color, MessageDecoration... decorations) {
        return getPlugin().createMessage().add(message, color, decorations);
    }

    public static McmeLogger getLogger() {
        return logger;
    }

    public static void setLogger(McmeLogger logger) {
        MiniGames.logger = logger;
    }

}
