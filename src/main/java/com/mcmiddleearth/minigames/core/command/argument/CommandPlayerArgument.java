package com.mcmiddleearth.minigames.core.command.argument;

import com.mcmiddleearth.base.core.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.base.core.player.McmeProxyPlayer;
import com.mcmiddleearth.minigames.core.MiniGames;
import com.mcmiddleearth.minigames.core.util.PluginData;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.stream.Collectors;

public class CommandPlayerArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readUnquotedString();
        McmeProxyPlayer player = MiniGames.getProxy().getPlayer(o);
        if(PluginData.getGame(player) != null){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandPlayerArgument")),
                new LiteralMessage(String.format("Player not inside a game: "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet());
    }
}
