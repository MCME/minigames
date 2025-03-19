package com.mcmiddleearth.minigames.command.argument;

import com.mcmiddleearth.command.argument.AbstractPlayerArgumentType;
import com.mcmiddleearth.minigames.velocity.MiniGamesPlugin;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.util.Permission;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.velocitypowered.api.proxy.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandPermmisionArgument extends AbstractPlayerArgumentType {

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException{
        String o = reader.readUnquotedString();
        Optional<Player> oPlayer = MiniGamesPlugin.getInstance().getProxyServer().getPlayer(o);
        MinigameCommandSender player =  oPlayer.map(MinigameCommandSender::new).orElseGet(() -> null);
        if(PluginData.getGame(player) != null && PluginData.hasPermission(player, Permission.MANAGER)){
            return o;
        }
        throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Failed parsing of CommandPermmisionArgument")),
                new LiteralMessage(String.format("Player not inside a game or has the correct permission: "+o)));
    }

    @Override
    protected Collection<String> getPlayerSuggestions() {
        return PluginData.getManagerPerms().stream().collect(Collectors.toSet());
    }
}
