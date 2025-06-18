package com.mcmiddleearth.minigames.scoreboard.generics;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ScoreboardObjective {
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("minigames:scoreboard_objective");
    public enum Position{DISABLED, LIST, SIDE, BELOW}

    private String objectiveName;
    private Component displayName;
    private Position position;

    public void setObjectiveName(String objectiveName){
        this.objectiveName = objectiveName;
    }

    public String getObjectiveName(){
        return objectiveName;
    }

    public void setDisplayName(Component displayName){
        this.displayName = displayName;
    }

    public void setPosition(Position position){
        this.position = position;
    }

    public byte[] toByteArray(String scoreboardName, CUD action){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(action.ordinal());
        if(action == CUD.DELETE)
            return out.toByteArray();
        out.writeUTF(scoreboardName);
        out.writeUTF(objectiveName);
        out.writeUTF(LegacyComponentSerializer.legacySection().serialize(displayName));
        out.writeInt(position.ordinal());
        return out.toByteArray();
    }
}
