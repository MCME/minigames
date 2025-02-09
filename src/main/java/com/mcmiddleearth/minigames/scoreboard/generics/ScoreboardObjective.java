package com.mcmiddleearth.minigames.scoreboard.generics;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;

import java.util.Locale;

public class ScoreboardObjective {
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("minigames:scoreboard_objective");

    private String name;
    private Component value;
    private HealthDisplay type;

    private CRUD action;

    public void setName(String name){
        this.name = name;
    }
    public void setValue(Component value){
        this.value = value;
    }

    public void setType(HealthDisplay type){
        this.type = type;
    }

    public void setAction(CRUD action){
        this.action = action;
    }

    public enum HealthDisplay
    {

        INTEGER, HEARTS;

        @Override
        public String toString()
        {
            return super.toString().toLowerCase( Locale.ROOT );
        }

        public static HealthDisplay fromString(String s)
        {
            return valueOf( s.toUpperCase( Locale.ROOT ) );
        }
    }
}
