package com.mcmiddleearth.minigames.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.MiniGames;
import com.mcmiddleearth.minigames.SpigotChannels;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class QuizListener implements PluginMessageListener, Listener {
    Set<Player> playersInConversation = new HashSet<>();
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if(!channel.equals(SpigotChannels.QUIZ)){
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String command = in.readUTF();
        if(command.equals("start_conversation"))
            playersInConversation.add(player);
    }

    @EventHandler
    public void onMessage(PlayerChatEvent e){
        Player player = e.getPlayer();
        String message = e.getMessage();
        if(playersInConversation.contains(player)){
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("answered");
            out.writeUTF(message);
            player.sendPluginMessage(MiniGames.getInstance(), SpigotChannels.QUIZ, out.toByteArray());
            playersInConversation.remove(player);
            e.setCancelled(true);
        }
    }
}
