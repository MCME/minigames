package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.bungee.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author Jubo
 */
public class MinigamesPluginListenerPaper implements PluginMessageListener {

    public MinigamesPluginListenerPaper(){}

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals(Channel.MAIN)){
            return;
        }

    }
}
