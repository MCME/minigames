package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.command.handler.AbstractCommandHandler;
import com.mcmiddleearth.command.sender.McmeCommandSender;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mojang.brigadier.Command;
import com.velocitypowered.api.proxy.Player;
import org.bukkit.command.TabExecutor;

/**
 *
 * @author Jubo
 */
public class MinigamesPluginCommand implements Command, TabExecutor
 {

    private AbstractCommandHandler handler;
    private String name;

    public MinigamesPluginCommand(AbstractCommandHandler handler, String name){
        super(name);
        this.name = name;
        this.handler = handler;
    }

    @Override
    public void execute(Player sender, String[] args) {
        McmeCommandSender wrappedSender = MiniGamesPlugin.wrapCommandSender(sender);
        handler.execute(wrappedSender,args);
    }

    @Override
    public Iterable<String> onTabComplete(Player sender, String[] args) {
        TabCompleteRequest request = new SimpleTabCompleteRequest(MiniGamesPlugin.wrapCommandSender(sender),String.format("/%s %s",name,String.join(" ",args)));
        handler.onTabComplete(request);
        return request.getSuggestions();
    }
}
