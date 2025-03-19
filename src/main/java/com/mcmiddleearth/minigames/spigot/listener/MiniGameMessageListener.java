package com.mcmiddleearth.minigames.spigot.listener;

import com.mcmiddleearth.minigames.common.Channels;
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
        if(!channel.equals(Channels.MAIN.getId())){
            return;
        }
    }
}
