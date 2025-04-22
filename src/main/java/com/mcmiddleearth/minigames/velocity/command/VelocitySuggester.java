package com.mcmiddleearth.minigames.velocity.command;

import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
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

    public static CompletableFuture<Suggestions> TimeLimitArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        if(builder.getRemaining().isBlank())
            builder.suggest("Time limit in seconds");
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> NewFileNameArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        if(builder.getRemaining().isBlank())
            builder.suggest("Write out the file name you wish to use (one word and don't use special characters)");
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> DescriptionArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        if(builder.getRemaining().isBlank())
            builder.suggest("Write out the description you wish to use (can be multiple words)");
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> ExistingQuizFileArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
         //TODO: load correct filenames

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> CategoryArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        String options = "abcdefghijklmnopqrstuvwxyz";
        String current = builder.getRemaining();

        for(char option : options.toCharArray()){
            if(!current.contains(option + ""))
                builder.suggest(current + option);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> QuestionRandomTYpeArgument(CommandContext<CommandSource> c, SuggestionsBuilder builder) {
        Set<String> options = Set.of("off", "questions", "choices", "all");
        options.forEach(option ->{
            if(option.startsWith(builder.getRemaining()))
                builder.suggest(option);
        });
        return builder.buildFuture();
    }
}
