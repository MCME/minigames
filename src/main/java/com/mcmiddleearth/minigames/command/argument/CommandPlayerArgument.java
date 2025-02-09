package com.mcmiddleearth.minigames.command.argument;

import com.mcmiddleearth.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.velocitypowered.api.proxy.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandPlayerArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readUnquotedString();
        Optional<Player> player = MiniGamesPlugin.getInstance().getProxyServer().getPlayer(o);
        if(player.isPresent() && PluginData.getGame(player.get()) != null){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandPlayerArgument")),
                new LiteralMessage(String.format("Player not inside a game: "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return MiniGamesPlugin.getInstance().getProxyServer().getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toSet());
    }
}
