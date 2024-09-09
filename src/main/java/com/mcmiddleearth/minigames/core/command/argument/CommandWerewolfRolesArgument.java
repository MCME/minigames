package com.mcmiddleearth.minigames.core.command.argument;

import com.mcmiddleearth.base.core.command.argument.AbstractPlayerArgumentType;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.ArrayList;
import java.util.Collection;

public class CommandWerewolfRolesArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readUnquotedString();
        if(true){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandWerewolfRolesArgument")),
                new LiteralMessage(String.format("Not valid: "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return new ArrayList<>();
    }
}
