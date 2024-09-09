package com.mcmiddleearth.minigames.bungee.command;

import com.mcmiddleearth.base.core.command.McmeCommandSender;
import com.mcmiddleearth.base.core.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.base.core.command.TabCompleteRequest;
import com.mcmiddleearth.base.core.command.handler.AbstractCommandHandler;
import com.mcmiddleearth.minigames.bungee.MiniGamesBungeePlugin;
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
        McmeCommandSender wrappedSender = MiniGamesBungeePlugin.wrapCommandSender(sender);
        handler.execute(wrappedSender,args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        TabCompleteRequest request = new SimpleTabCompleteRequest(MiniGamesBungeePlugin.wrapCommandSender(sender),String.format("/%s %s",name,String.join(" ",args)));
        handler.onTabComplete(request);
        return request.getSuggestions();
    }
}
