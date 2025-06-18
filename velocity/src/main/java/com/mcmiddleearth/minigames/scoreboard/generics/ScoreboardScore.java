package com.mcmiddleearth.minigames.scoreboard.generics;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ScoreboardScore {
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("minigames:scoreboard_score");

    private String objectiveName;
    private String scoreName;
    private Component displayName;
    private Integer value;

    public void setObjectiveName(String objectiveName){
        this.objectiveName = objectiveName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public void setDisplayName(Component displayName){
        this.displayName = displayName;
    }

    public void setValue(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }

    public void updateValue(Integer update){
        this.value += update;
    }

    public byte[] toByteArray(String scoreboardName, CUD action){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(action.ordinal());
        if(action == CUD.DELETE)
            return out.toByteArray();
        out.writeUTF(scoreboardName);
        out.writeUTF(objectiveName);
        out.writeUTF(scoreName);
        out.writeUTF(LegacyComponentSerializer.legacySection().serialize(displayName));
        out.writeInt(value);
        return out.toByteArray();
    }
}
