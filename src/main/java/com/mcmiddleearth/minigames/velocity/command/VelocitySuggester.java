package com.mcmiddleearth.minigames.velocity.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.CommandSource;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class VelocitySuggester {
    public static CompletableFuture<Suggestions> HelpArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        Set<String> options = Set.of("general", "hide", "race", "quiz");
        options.forEach(option -> {
            if(option.startsWith(builder.getRemaining()))
                builder.suggest(option);
        } );
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> GameTypeArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        Set<String> options = Set.of("quiz");
        options.forEach(option -> {
            if(option.startsWith(builder.getRemaining()))
                builder.suggest(option);
        });
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> QuestionTypeArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        Set<String> options = Set.of("single", "multi", "free", "number");
        options.forEach(option ->{
            if(option.startsWith(builder.getRemaining()))
                builder.suggest(option);
        });
        return builder.buildFuture();
    }
}
