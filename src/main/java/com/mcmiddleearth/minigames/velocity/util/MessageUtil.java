package com.mcmiddleearth.minigames.velocity.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcmiddleearth.minigames.common.Channels;
import com.velocitypowered.api.proxy.Player;

import java.io.ByteArrayOutputStream;
import java.util.Set;

public class MessageUtil {
    static public void sendMessage(Set<Player> players, String message){
        players.forEach(player -> sendMessage(player, message));
    }

    static public void sendMessage(Player player, String message){
        String username = player.getUsername();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Message");
        out.writeUTF(username);
        out.writeUTF(message);
        player.getCurrentServer().ifPresent(connection ->
                connection.sendPluginMessage(Channels.BUNGEE, out.toByteArray()));
    }
}
