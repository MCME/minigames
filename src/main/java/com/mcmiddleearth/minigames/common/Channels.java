package com.mcmiddleearth.minigames.common;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

/**
 *
 * @author Jubo
 */
public class Channels {
    //TODO: split into two files spigot and velocity
    public static final MinecraftChannelIdentifier MAIN = MinecraftChannelIdentifier.from("mcme:minigames");

    public static final MinecraftChannelIdentifier QUIZ = MinecraftChannelIdentifier.from("quiz:main");

    public static final MinecraftChannelIdentifier GAMEMANAGER = MinecraftChannelIdentifier.from("minigames:gamemanager");
}
