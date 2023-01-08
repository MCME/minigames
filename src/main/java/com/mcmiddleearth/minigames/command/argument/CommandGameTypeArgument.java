package com.mcmiddleearth.minigames.command.argument;

import com.mcmiddleearth.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.minigames.game.GameType;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandGameTypeArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readUnquotedString();
        if(GameType.getGameType(o) != null){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandGameTypeArgument")),
                new LiteralMessage(String.format("Not a valid gametype "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return GameType.toList();
    }
}
