package com.mcmiddleearth.minigames.core.command.argument;

import com.mcmiddleearth.base.core.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.minigames.bungee.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.core.MiniGames;
import com.mcmiddleearth.minigames.core.util.Permission;
import com.mcmiddleearth.minigames.core.util.PluginData;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.md_5.bungee.api.ProxyServer;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CommandPermmisionArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException{
        String o = reader.readUnquotedString();
        McmeProxyPlayer player =  MiniGames.getProxy().getPlayer(o);
        if(PluginData.getGame(player) != null && PluginData.hasPermission(player, Permission.MANAGER)){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandPermmisionArgument")),
                new LiteralMessage("Player not inside a game or has the correct permission: " + o));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return new HashSet<>(PluginData.getManagerPerms());
    }
}
