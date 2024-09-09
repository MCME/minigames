package com.mcmiddleearth.minigames.core.command.argument;

import com.mcmiddleearth.base.core.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.minigames.core.quiz.question.QuestionType;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;

public class CommandQuestionTypeArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String o = reader.readUnquotedString();
        if(QuestionType.getQuestionType(o) != null){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandQuestionTypeArgument")),
                new LiteralMessage(String.format("Not a valid questiontype "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return QuestionType.toList();
    }
}
