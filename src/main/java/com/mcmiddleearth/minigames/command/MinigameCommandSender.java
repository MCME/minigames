package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Jubo
 */
public class MinigameCommandSender implements McmeCommandSender {

    private final Player sender;

    public static HashMap<UUID,MinigameCommandSender> players = new HashMap<>();

    public MinigameCommandSender(Player sender){
        this.sender = sender;
    }

    public Player getCommandSender(){
        return sender;
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        sender.sendMessage(Component.text(BaseComponent.toLegacyText(baseComponents)));
    }

    @Override
    public boolean hasPermission(String s) {
        return sender.hasPermission(s);
    }

    public static String getName(MinigameCommandSender sender){
        return sender.getCommandSender().getUsername();
    }

    public static MinigameCommandSender getOrCreateMcmePlayer(Player player){
        return players.computeIfAbsent(player.getUniqueId(), k -> new MinigameCommandSender(player));
    }

    public static void removeMcmePlayer(Player player){
        players.remove(player.getUniqueId());
    }
}
