package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 *
 * @author Jubo
 */
public class MinigamesPluginCommand extends Command implements TabExecutor {

    private AbstractCommandHandler handler;
    private String name;

    public MinigamesPluginCommand(AbstractCommandHandler handler, String name){
        super(name);
        this.name = name;
        this.handler = handler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        McmeCommandSender wrappedSender = MiniGamesPlugin.wrapCommandSender(sender);
        handler.execute(wrappedSender,args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        TabCompleteRequest request = new SimpleTabCompleteRequest(MiniGamesPlugin.wrapCommandSender(sender),String.format("/%s %s",name,String.join(" ",args)));
        handler.onTabComplete(request);
        return request.getSuggestions();
    }
}
