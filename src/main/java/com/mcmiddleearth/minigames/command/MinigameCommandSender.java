package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.command.McmeCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Jubo
 */
public class MinigameCommandSender implements McmeCommandSender {

    private final CommandSender sender;

    public static HashMap<UUID,MinigameCommandSender> players = new HashMap<>();

    public MinigameCommandSender(CommandSender sender){
        this.sender = sender;
    }

    public CommandSender getCommandSender(){
        return sender;
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        sender.sendMessage(baseComponents);
    }

    public static String getName(MinigameCommandSender sender){
        return sender.getCommandSender().getName();
    }

    public static MinigameCommandSender getOrCreateMcmePlayer(ProxiedPlayer player){
        MinigameCommandSender result = players.get(player.getUniqueId());
        if(result == null){
            result = new MinigameCommandSender(player);
            players.put(player.getUniqueId(),result);
        }
        return result;
    }

    public static void removeMcmePlayer(ProxiedPlayer player){
        players.remove(player.getUniqueId());
    }
}
