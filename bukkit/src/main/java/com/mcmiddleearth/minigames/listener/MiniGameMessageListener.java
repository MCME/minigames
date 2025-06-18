package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.SpigotChannels;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Jubo
 */
public class MiniGameMessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if(!channel.equals(SpigotChannels.MAIN)){
            return;
        }
    }
}
