package com.mcmiddleearth.minigames;

import java.util.ArrayList;
import java.util.List;

public enum GameType {
    HIDE_AND_SEEK   ("Hide"),
    RACE            ("Race"),
    LORE_QUIZ       ("Quiz"),
    GEO_GUESSR      ("Geo"),
    MANHUNT         ("Manhunt"),
    WEREWOLF        ("Werewolf");

    private final String name;

    GameType(String name) {
        this.name = name;
    }

    public static GameType getGameType(String name) {
        for(GameType type: GameType.values()) {
            if(type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static List<String> toList(){
        List<String> names = new ArrayList<>();
        for(GameType type: GameType.values())
            names.add(type.name);
        return names;
    }
}
