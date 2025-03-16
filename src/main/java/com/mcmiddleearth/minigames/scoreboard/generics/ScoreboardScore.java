package com.mcmiddleearth.minigames.scoreboard.generics;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ScoreboardScore {

    private String objectiveName;
    private String scoreName;
    private Integer value;

    public void setObjectiveName(String objectiveName){
        this.objectiveName = objectiveName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public void setValue(Integer value){
        this.value = value;
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
        out.writeInt(value);
        return out.toByteArray();
    }
}
