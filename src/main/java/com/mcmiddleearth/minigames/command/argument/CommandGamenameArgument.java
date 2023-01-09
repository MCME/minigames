package com.mcmiddleearth.minigames.command.argument;

import com.mcmiddleearth.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;
import java.util.stream.Collectors;

public class CommandGamenameArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readUnquotedString();
        if(PluginData.getGame(o) != null){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandGamenameArgument")),
                new LiteralMessage(String.format("No game with this name: "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return PluginData.getGames().stream().collect(Collectors.toSet());
    }
}
