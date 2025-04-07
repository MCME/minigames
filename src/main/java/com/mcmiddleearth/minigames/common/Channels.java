package com.mcmiddleearth.minigames.common;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

/**
 *
 * @author Jubo
 */
public class Channels {
    public static final String MAIN_PAPER = "mcme:minigames";
    public static final MinecraftChannelIdentifier MAIN = MinecraftChannelIdentifier.from(MAIN_PAPER);

    public static final MinecraftChannelIdentifier BUNGEE = MinecraftChannelIdentifier.from("bungeecord:main");
}
