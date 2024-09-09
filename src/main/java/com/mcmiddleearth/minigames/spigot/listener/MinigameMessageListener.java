package com.mcmiddleearth.minigames.spigot.listener;

import com.mcmiddleearth.minigames.core.util.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Jubo
 */
public class MinigameMessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if(!channel.equals(Channel.MAIN)){
            return;
        }
    }
}
