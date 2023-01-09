package com.mcmiddleearth.minigames.command.argument;

import com.mcmiddleearth.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;

/**
 * @author Jubo
 */
public class CommandAllowDenyArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readUnquotedString();
        if(AbstractGame.getConfig().keySet().contains(o)){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandAllowDenyArgument")),
                new LiteralMessage(String.format("Not valid: "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return AbstractGame.getConfig().keySet();
    }
}
