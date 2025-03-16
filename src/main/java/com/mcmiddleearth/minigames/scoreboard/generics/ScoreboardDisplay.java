package com.mcmiddleearth.minigames.scoreboard.generics;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public class ScoreboardDisplay {
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("minigames:scoreboard_display");

    public enum Position{LIST, SIDE, BELOW}

    private Position position;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public byte[] toByteArray(CUD action){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(action.ordinal());
        if(action == CUD.DELETE)
            return out.toByteArray();
        out.writeUTF(name);
        out.writeInt(position.ordinal());
        return out.toByteArray();
    }
}
